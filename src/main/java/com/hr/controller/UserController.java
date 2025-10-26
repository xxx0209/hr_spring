package com.hr.controller;

import com.hr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println(userDetails.getAuthorities());
        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "memberId", userDetails.getMemberId(),
                "name", userDetails.getName(),
                "email", userDetails.getEmail(),
                "roles", userDetails.getAuthorities()
        ));
    }
}
