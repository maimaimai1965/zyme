package ua.mai.zyme.security.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Фильтр обрабатывающий только URL <i>"/hiWithStaticKey"</i>.
 * Для запросов по этому URL проверяется ключ авторизации. Если ключ правильный, то добавляется объект Authentication с
 * именем UserWithAuthorizationKey и ролями ROLE_RD, ROLE_WR, а если неправильный, то возвращается ответ со статусом
 * <i>401 Unauthorized</i>.
 */
@Component
public class StaticKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${authorization.key}")
    private String authorizationKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
              throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/hiWithStaticKey")) {
            // Если это URL, который доступен по Static Key.
            String requestAuthorizationKey = request.getHeader("Authorization");

            if (authorizationKey.equals(requestAuthorizationKey)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken("UserWithAuthorizationKey", "static-key",
                                List.of(new SimpleGrantedAuthority("ROLE_RD"), new SimpleGrantedAuthority("ROLE_WR")));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            // Остальные URL доступны только по Basic аутентификации (уже обязательно должен быть Authentication объект).
            if (SecurityContextHolder.getContext().getAuthentication() == null)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        // Фильтр применяется только для URL /hyWithStaticKey* (см. StaticKeyAuthenticationFilter.configure()).
//        return !request.getRequestURI().startsWith("/hiWithStaticKey");
//    }

}
