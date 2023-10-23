package ua.mai.zyme.rest.zoo.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger("rest.zoo.auth");

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    @GetMapping("/login")
    public String login(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection authorities = authentication.getAuthorities();
        String authoritiesStr = (authorities == null || authorities.size() == 0) ? "" : StringUtils.join(authorities, ",");

        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder()
                .setClaims(Map.of("username", username,
                                  "authorities", authoritiesStr))
                .signWith(key)
                .compact();
        LOG.debug("AuthController.login(): Build JWT token for username='{}', authorities=[{}]: '{}'", username, authorities, jwt);

        response.setHeader("Authorization", jwt);
        return "JWT token generated for user " + username;
    }

}
