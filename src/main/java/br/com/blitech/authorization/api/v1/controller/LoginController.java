package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.LoginUserInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
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

@RestController
@RequestMapping(path = "/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

    @Autowired
    private JwtKeyProvider jwtKeyProvider;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @RateLimit
    @LogAndValidate
    @PostMapping("/application")
    public LoginModel loginApplication(@NotNull @RequestBody LoginApplicationInputModel loginApplicationInputModel) throws UserNotAuthorizedException {
        try {
            var application = applicationService.validateLogin(loginApplicationInputModel.getUsername(), loginApplicationInputModel.getPassword());
            var authorities = applicationService.getApplicationAuthorities(application.getId());
            return new LoginModel(generateToken(application.getUser(), application.getName(), authorities));
        } catch (ApplicationNotFoundException | UserInvalidPasswordException e) {
            throw new UserNotAuthorizedException();
        }
    }

    @RateLimit
    @LogAndValidate
    @PostMapping("/user")
    public LoginModel loginUser(@NotNull @RequestBody LoginUserInputModel loginUserInputModel) throws BusinessException {
        try {
            var application = applicationService.findByNameOrThrow(loginUserInputModel.getApplication());
            var authorities = userService.getUserAuthorities(
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

    public String generateToken(String email, String application, Set<String> authorities) {
        Date date = new Date();
        Date expiration = new Date(date.getTime() + 3600000);

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("scopes", Arrays.asList("READ", "WRITE"));
        claims.put("authorities", authorities);
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
}
