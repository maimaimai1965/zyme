package ua.mai.zyme.security.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * Фильтр проверяющий JWT токен в запросе.
 * Не проверяются URLs "/login" (для него используется BASIC аутентификация) и "/h2-console" (для него не используется
 * никакая аутентификация - это URL для доступа к консоле H2).
 * </pre>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger("Authentication");

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");

        if (jwt == null) {
            LOG.debug("JwtAuthenticationFilter: JWT token is absent");
            throw new InsufficientAuthenticationException("JWT token is absent");
        }
        LOG.debug("JwtAuthenticationFilter: Parse JWT token {}", jwt);

        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        String username = String.valueOf(claims.get("username"));
        String authoritiesStr = (String) claims.get("authorities");
        List<GrantedAuthority> authorities = (authoritiesStr == null || authoritiesStr.isEmpty())
                ? new ArrayList<>()
                : Arrays.stream(StringUtils.splitPreserveAllTokens(authoritiesStr, ","))
                        .map(auth -> new SimpleGrantedAuthority(auth)).collect(Collectors.toList());
        LOG.debug("JwtAuthenticationFilter: Parsed JWT token - username='{}', authorities={}", username, authorities);

        if (authorities.isEmpty()) {
            // Если для пользователя не определена никакая роль, то определяем для него роль ROLE_UNDEF.
            authorities.add(new SimpleGrantedAuthority("ROLE_UNDEF"));
            LOG.debug("JwtAuthenticationFilter: Add ROLE_UNDEF authority");
        }

        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login") ||         // Для URL "/login" используется BASIC аутентификация.
               request.getServletPath().startsWith("/h2-console") ; // Для консоли H2 не используется GWT аутентификация.
    }

}
