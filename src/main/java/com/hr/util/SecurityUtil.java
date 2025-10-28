package com.hr.util;

import com.hr.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    /**
     * 현재 로그인된 사용자의 username (ID) 반환
     */
    public static String getLoginUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    /**
     * 현재 로그인된 사용자의 CustomUserDetails 반환
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }

        return null;
    }

    /**
     * 로그인한 사용자의 아이디 가져오기
     */
    public static String getLoginMemberId() {
        CustomUserDetails user = getCurrentUser();
        return (user != null) ? user.getMemberId() : null;
    }

    /**
     * 로그인한 사용자의 이름 가져오기
     */
    public static String getLoginName() {
        CustomUserDetails user = getCurrentUser();
        return (user != null) ? user.getName() : null;
    }
}

