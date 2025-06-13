package br.com.blitech.authorization.api.filter;

import br.com.blitech.authorization.api.exceptionhandler.ApiAuthenticationEntryPoint;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtKeyProvider jwtKeyProvider;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(JwtKeyProvider jwtKeyProvider, ApiAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtKeyProvider = jwtKeyProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(request));
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid JWT token", e));
        }
    }

    @Nullable
    private Authentication getAuthentication(@NotNull HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String token = header.replace("Bearer", "").trim();
        PublicKey publicKey = jwtKeyProvider.getPublicKey();

        Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token);

        String userName = claimsJws.getBody().getSubject();
        request.setAttribute("username", userName);

        String application = claimsJws.getBody().get("application").toString();
        request.setAttribute("application", application);

        List<?> authoritiesList = (List<?>) claimsJws.getBody().get("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesList.stream()
            .map(auth -> new SimpleGrantedAuthority(auth.toString()))
            .toList();

        return new UsernamePasswordAuthenticationToken(userName, null, authorities);
    }
}
