package com.hr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//한글깨지나
@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping({"/", "/home"})
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("home");
    }
}
