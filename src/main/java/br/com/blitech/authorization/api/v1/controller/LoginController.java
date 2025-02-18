package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimitAspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.LoginUserInputModel;
import br.com.blitech.authorization.api.v1.openapi.LoginControllerOpenApi;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
import br.com.blitech.authorization.domain.service.ServiceUserService;
import br.com.blitech.authorization.domain.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController implements LoginControllerOpenApi {
    private final JwtKeyProvider jwtKeyProvider;
    private final ApplicationService applicationService;
    private final ServiceUserService serviceUserService;
    private final UserService userService;

    @Autowired
    public LoginController(JwtKeyProvider jwtKeyProvider, ApplicationService applicationService, UserService userService, ServiceUserService serviceUserService) {
        this.jwtKeyProvider = jwtKeyProvider;
        this.applicationService = applicationService;
        this.userService = userService;
        this.serviceUserService = serviceUserService;
    }

    @Override
    @RateLimit
    @LogAndValidate
    @PostMapping("/application")
    public LoginModel loginApplication(@NotNull @RequestBody LoginApplicationInputModel loginApplicationInputModel) throws UserNotAuthorizedException {
        try {
            var application = applicationService.validateLogin(loginApplicationInputModel.getUsername(), loginApplicationInputModel.getPassword());
            var authorities = applicationService.getAuthorities(application.getId());
            return new LoginModel(generateToken(application.getUser(), application.getName(), authorities));
        } catch (ApplicationNotFoundException | UserInvalidPasswordException e) {
            throw new UserNotAuthorizedException();
        }
    }

    @Override
    @RateLimit
    @LogAndValidate
    @PostMapping("/user")
    public LoginModel loginUser(@NotNull @RequestBody LoginUserInputModel loginUserInputModel) throws BusinessException {
        try {
            var application = applicationService.findByNameOrThrow(loginUserInputModel.getApplication());
            var authorities = userService.getAuthorities(
                    application,
                    loginUserInputModel.getUsername(),
                    loginUserInputModel.getPassword()
            );
            return new LoginModel(generateToken(loginUserInputModel.getUsername(), application.getName(), authorities));
        } catch (UserInvalidPasswordException e) {
            throw new UserNotAuthorizedException();
        } catch (ApplicationNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @RateLimit
    @LogAndValidate
    @PostMapping("/service-user")
    public LoginModel loginServiceUser(@NotNull @RequestBody LoginUserInputModel loginUserInputModel) throws BusinessException {
        try {
            var user = serviceUserService.validateLogin(loginUserInputModel.getApplication(), loginUserInputModel.getUsername(), loginUserInputModel.getPassword());
            var authorities = serviceUserService.getAuthorities(user.getId(), user.getApplication().getId());
            return new LoginModel(generateToken(loginUserInputModel.getUsername(), user.getApplication().getName(), authorities));
        } catch (UserInvalidPasswordException e) {
            throw new UserNotAuthorizedException();
        } catch (ApplicationNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private String generateToken(String email, String application, @NotNull Set<String> authorities) {
        Date date = new Date();
        Date expiration = new Date(date.getTime() + 3600000);

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");

        var scopes = authorities.stream()
            .map(s -> s.split("\\.")[1])
            .collect(Collectors.toSet());

        Map<String, Object> claims = new HashMap<>();
        claims.put("scopes", sortSet(scopes));
        claims.put("authorities", sortSet(authorities));
        claims.put("application", application);

        return Jwts.builder()
                .setHeader(headers)
                .setSubject(email)
                .addClaims(claims)
                .setIssuedAt(date)
                .setExpiration(expiration)
                .signWith(jwtKeyProvider.getKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private Set<String> sortSet(@NotNull Set<String> set) {
        return set.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
