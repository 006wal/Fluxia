package com.fluxia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/learn-more")
    public String learnMore() {
        return "learn-more";
    }
}
