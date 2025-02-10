package com.example.example.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class JwtDto {

    private String token;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtDto(String token, String username, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.username = username;
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
