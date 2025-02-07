package br.com.blitech.authorization.api.filter;

import br.com.blitech.authorization.api.security.JwtKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtKeyProvider jwtKeyProvider;

    public JwtAuthenticationFilter(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication((HttpServletRequest) request));
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
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
            .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userName, null, authorities);
    }
}
