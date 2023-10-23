package ua.mai.zyme.security.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mai.zyme.security.services.AuthService;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger("Authentication");

    @Autowired
    private AuthService authService;

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection authorities = authentication.getAuthorities();
        String authoritiesStr = (authorities == null || authorities.size() == 0) ? "" : StringUtils.join(authorities, ",");

        String code = request.getHeader("code");

        if (code == null || code.isEmpty()) {
            // Если в Header-е нет параметра 'code', то генерируем его и добавляем его в Header с именем 'Authorization'..
            code = authService.authByOtp(username);
            response.addHeader("code", code);
            LOG.debug("Generate 'code' header for getting GWT token.", code);
            return "Code generated for user " + username;

        } else {
            // Если в Header-е есть параметр 'code', то проверяем его, генерируем GWT токен и добавляем его в Header с именем 'Authorization'.
            if (!authService.checkOtp(username, code)) {
                LOG.debug("AuthController: Bad code for username='{}'", username);
                throw new BadCredentialsException("Bad credentials.");
            }

            SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
            String jwt = Jwts.builder()
                    .setClaims(Map.of("username", username,
                                      "authorities", authoritiesStr))
                    .signWith(key)
                    .compact();
            response.setHeader("Authorization", jwt);
            LOG.debug("AuthController: Build JWT token by code='{}' for username='{}', authorities={}: '{}'", code, username, authorities, jwt);

            return "JWT token generated for user " + username;
        }
    }

}
