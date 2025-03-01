package com.example.pokerbackend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class test {

    // Define a GET endpoint
    @GetMapping("/greet")
    public String greet() {
        return "Hello, world!";
    }

    // Define a POST endpoint
    @PostMapping("/greet")
    public String greetWithName(@RequestBody String name) {
        return "Hello, " + name + "!";
    }
}