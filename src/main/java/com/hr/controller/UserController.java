package com.hr.controller;

import com.hr.constant.MemberRole;
import com.hr.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        String role = MemberRole.ROLE_USER.getValue();
        if (!authorities.isEmpty()) {
            GrantedAuthority firstAuthority = authorities.iterator().next();
            role = firstAuthority.getAuthority();
            System.out.println("첫 번째 권한: " + role);
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "memberId", userDetails.getMemberId(),
                "name", userDetails.getName(),
                "email", userDetails.getEmail(),
                "roles", userDetails.getAuthorities(),
                "role" , role
        ));
    }
}
