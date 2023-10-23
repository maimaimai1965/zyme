package ua.mai.zyme.security.oauth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZooController {

    @GetMapping("/zoo/animals")
    public String[] getAnimals() {
        return new String[]{"tiger", "giraffe", "crocodile"};
    }

    @GetMapping("/zoo/tanks")
    public String[] getTanks() {
        return new String[]{"tank with tigers", "tank with giraffes", "tank with crocodiles", "empty tank"};
    }

}