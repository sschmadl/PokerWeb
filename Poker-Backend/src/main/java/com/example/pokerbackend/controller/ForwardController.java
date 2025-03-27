package com.example.pokerbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForwardController {
    @GetMapping("/{path:^(?!ws$)[^\\.]*}")  // Verhindert die Weiterleitung von "/ws"
    public String forward() {
        return "forward:/index.html";
    }
}
