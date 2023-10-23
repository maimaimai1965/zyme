package ua.mai.zyme.security.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/addWithCheckCsrf")
    public String addWithCheckCsrf(@RequestParam String name) {
        LOG.info("Adding product {} (with check CSRF)", name);
        return "added.html";
    }

    @PostMapping("/addWithoutCheckCsrf")
    public String addWithCsrf(@RequestParam String name) {
        LOG.info("Adding product {} (without check CSRF)", name);
        return "added.html";
    }

}
