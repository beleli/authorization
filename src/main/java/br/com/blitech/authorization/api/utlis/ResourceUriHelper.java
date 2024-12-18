package br.com.blitech.authorization.api.utlis;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Component
public class ResourceUriHelper {

	@Autowired
	private ApplicationService applicationService;

	public static void addUriInResponseHeader(Object resourceId) {
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
			.path("/{id}")
			.buildAndExpand(resourceId).toUri();
		
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        response.setHeader(HttpHeaders.LOCATION, uri.toString());
	}

	@NotNull
	public static String getUsername(@NotNull HttpServletRequest request) {
		var username = request.getAttribute("username");
		return username != null ? username.toString() : "";
	}

	@NotNull
	public static String getApplication(@NotNull HttpServletRequest request) {
		var application = request.getAttribute("application");
		return application != null ? application.toString() : "";
	}

	@Transactional(readOnly = true)
	public boolean isYourselfApplication(@NotNull Long applicationId) {
		var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Application application;
        try {
            application = applicationService.findByNameOrThrow(getApplication(request));
        } catch (ApplicationNotFoundException e) {
            return false;
        }
        return applicationId.equals(application.getId()) && getUsername(request).equalsIgnoreCase(application.getUser());
	}
}
