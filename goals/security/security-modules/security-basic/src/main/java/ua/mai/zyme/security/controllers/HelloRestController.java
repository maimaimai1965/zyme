package ua.mai.zyme.security.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class HelloRestController {

    @GetMapping("/hello")
    public String hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection authorities = authentication.getAuthorities();

        return "hello() " + username + " with roles " + authorities + "!";
    }

    @GetMapping("/hiWithStaticKey")
    public String hiWithStaticKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection authorities = authentication.getAuthorities();

        return "hiWithStaticKey() " + username + " with roles " + authorities + "!";
    }
}
