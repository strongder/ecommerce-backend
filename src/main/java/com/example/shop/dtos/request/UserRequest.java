package com.example.shop.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String avatar;
    private String fullName;
    private String email;
    private String phone;
    private Set<String> roles;
}
