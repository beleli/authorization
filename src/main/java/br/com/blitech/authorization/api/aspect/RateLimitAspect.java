package br.com.blitech.authorization.api.aspect;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.service.CacheService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private CacheService cacheService;

    @Pointcut("@annotation(rateLimit)")
    public void validateMethod(RateLimit rateLimit) {
        // Pointcut definition
    }

    @Before("validateMethod(rateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, @NotNull RateLimit rateLimit) {
        String key = generateRateLimitKey();
        long periodInSeconds = rateLimit.timeUnit().toSeconds(rateLimit.timeValue());
        long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        RequestInfo requestInfo = cacheService.getKey(key, RequestInfo.class);
        List<Long> validRequests = new ArrayList<>();
        validRequests.add(currentTime);

        if (requestInfo != null) {
            validRequests.addAll(requestInfo.requests().stream()
                    .filter(requestEpoch -> requestEpoch >= (currentTime - periodInSeconds))
                    .toList());

            if (validRequests.size() > rateLimit.maxRequests()) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        Messages.get("api.rate-limit-exception")
                );
            }
        }

        cacheService.setKey(key, new RequestInfo(validRequests), Duration.ofSeconds(periodInSeconds));
    }

    private String generateRateLimitKey() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Request context is not available");
        }

        var request = attributes.getRequest();
        var username = ResourceUriHelper.getUsername(request);
        String identification = !username.isBlank()
                ? username
                : request.getRemoteAddr().replace(":", ".");

        return "rate_limit:" + identification.toLowerCase() + ":" + request.getRequestURI() + ":" + request.getMethod();
    }

    private record RequestInfo(List<Long> requests) { }
}
