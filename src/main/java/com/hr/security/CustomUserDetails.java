package com.hr.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    // 추가 정보 getter
    @Getter
    private String memberId;
    @Getter
    private String name;
    @Getter
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, String memberId, String name, String email,
                             Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.authorities = authorities;
    }

    private CustomUserDetails(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.memberId = builder.memberId;
        this.email = builder.email;
        this.name = builder.name;
        this.authorities = builder.authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // 편의 메소드
    public static Builder builder() {
        return new Builder();
    }

    // Builder 클래스
    public static class Builder {
        private String username;
        private String password;
        private String memberId;
        private String name;
        private String email;

        private Collection<? extends GrantedAuthority> authorities;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder memberId(String memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CustomUserDetails build() {
            return new CustomUserDetails(this);
        }
    }
}
