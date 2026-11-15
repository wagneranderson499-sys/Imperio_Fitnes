package br.com.imperiofitness;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String home() {
        return "forward:/home-v3.html";
    }

    @GetMapping({"/admin", "/admin/"})
    public String admin() {
        return "forward:/admin-v4.html";
    }
}
