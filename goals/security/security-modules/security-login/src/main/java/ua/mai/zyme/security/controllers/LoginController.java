package ua.mai.zyme.security.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/home")
    public String main(Authentication a, Model model) {
        model.addAttribute("username", a.getName());
        return "home.html";
    }

    @GetMapping("/no_auth")
    public String noAuth() {
        return "no_auth.html";
    }
}
