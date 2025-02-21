package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimitAspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginInputModel;
import br.com.blitech.authorization.api.v1.openapi.LoginControllerOpenApi;
import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationKeyNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationKeyService;
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

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController implements LoginControllerOpenApi {
    private final JwtKeyProvider jwtKeyProvider;
    private final ApplicationService applicationService;
    private final ApplicationKeyService applicationKeyService;
    private final ServiceUserService serviceUserService;
    private final UserService userService;

    @Autowired
    public LoginController(JwtKeyProvider jwtKeyProvider, ApplicationService applicationService, ApplicationKeyService applicationKeyService, UserService userService, ServiceUserService serviceUserService) {
        this.jwtKeyProvider = jwtKeyProvider;
        this.applicationService = applicationService;
        this.applicationKeyService = applicationKeyService;
        this.userService = userService;
        this.serviceUserService = serviceUserService;
    }

    @Override
    @RateLimit
    @LogAndValidate
    @PostMapping("/application")
    public LoginModel loginApplication(@NotNull @RequestBody LoginInputModel loginInputModel) throws UserNotAuthorizedException {
        try {
            var application = applicationService.validateLogin(loginInputModel.getApplication(), loginInputModel.getUsername(), loginInputModel.getPassword());
            var authorities = applicationService.getAuthorities(application.getId());
            return new LoginModel(generateToken(application.getUser(), application, authorities, true));
        } catch (ApplicationNotFoundException | ApplicationKeyNotFoundException | UserInvalidPasswordException e) {
            throw new UserNotAuthorizedException();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @RateLimit
    @LogAndValidate
    @PostMapping("/user")
    public LoginModel loginUser(@NotNull @RequestBody LoginInputModel loginInputModel) throws BusinessException {
        try {
            var application = applicationService.findByNameOrThrow(loginInputModel.getApplication());
            var authorities = userService.getAuthorities(
                    application,
                    loginInputModel.getUsername(),
                    loginInputModel.getPassword()
            );
            return new LoginModel(generateToken(loginInputModel.getUsername(), application, authorities, false));
        } catch (UserInvalidPasswordException | ApplicationNotFoundException e) {
            throw new UserNotAuthorizedException();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @RateLimit
    @LogAndValidate
    @PostMapping("/service-user")
    public LoginModel loginServiceUser(@NotNull @RequestBody LoginInputModel loginInputModel) throws BusinessException {
        try {
            var user = serviceUserService.validateLogin(loginInputModel.getApplication(), loginInputModel.getUsername(), loginInputModel.getPassword());
            var authorities = serviceUserService.getAuthorities(user.getApplication().getId(), user.getId());
            return new LoginModel(generateToken(loginInputModel.getUsername(), user.getApplication(), authorities, false));
        } catch (UserInvalidPasswordException | ApplicationNotFoundException e) {
            throw new UserNotAuthorizedException();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateToken(String username, @NotNull Application application, @NotNull Set<String> authorities, Boolean isDefaultKey) throws NoSuchAlgorithmException, InvalidKeySpecException, ApplicationKeyNotFoundException {
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
        claims.put("application", application.getName());

        Key key;
        if (isDefaultKey || application.getUseDefaultKey()) {
            key = jwtKeyProvider.getPrivateKey();
        } else {
            key = applicationKeyService.getLastPrivateKey(application);
        }

        return Jwts.builder()
                .setHeader(headers)
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(date)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.RS256)
                .compact();
    }

    private Set<String> sortSet(@NotNull Set<String> set) {
        return set.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
