package ua.mai.zyme.security.controllers;

import org.springframework.context.annotation.Role;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
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

    @GetMapping("/helloWithReadOrWriteAuthority")
    @RolesAllowed({"ROLE_READ", "ROLE_WRITE"})
    public String helloWithReadOrWriteAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection authorities = authentication.getAuthorities();

        return "helloWithReadOrWriteAuthority() " + username + " with roles " + authorities + "!";
    }

    @GetMapping("/helloWithDeleteAuthority")
    @RolesAllowed({"ROLE_DELETE"})
    public String helloWithDeleteAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection authorities = authentication.getAuthorities();

        return "helloWithDeleteAuthority() " + username + " with roles " + authorities + "!";
    }

}
