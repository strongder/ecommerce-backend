package com.example.shop.config.security;


import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.utils.AesUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserServiceDetail implements UserDetailsService {

	@Autowired
    private UserRepository userRepository;

    @Autowired
    AesUtil aesUtil;
    @Value("${base64.key}")
    private String base64Key;


    @PostConstruct
    private void init() {
        this.aesUtil = new AesUtil(base64Key);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return new CustomUserDetail(user.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
