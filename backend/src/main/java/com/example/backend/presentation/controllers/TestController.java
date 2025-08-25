package com.example.backend.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test API endpoints")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Simple hello endpoint", description = "Returns a simple hello message")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, World!");
    }

    @GetMapping("/hello/{name}")
    @Operation(summary = "Personalized hello endpoint", description = "Returns a personalized hello message")
    public ResponseEntity<String> helloWithName(@PathVariable String name) {
        return ResponseEntity.ok("Hello, " + name + "!");
    }
}