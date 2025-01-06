package com.example.shop.controller;

import com.example.shop.dtos.request.AuthRequest;
import com.example.shop.dtos.request.UserRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.AuthResponse;
import com.example.shop.dtos.response.UserResponse;
import com.example.shop.service.UserService;
import com.example.shop.utils.AesUtil;
import com.example.shop.utils.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    AesUtil aesUtil;

    @Value("${base64.key}")
    private String base64Key;


    @PostConstruct
    private void init() {
        this.aesUtil = new AesUtil(base64Key);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        log.info("authentication: {}", authentication.getAuthorities());
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            String token = jwtUtils.generateToken(authRequest.getEmail());
            String refreshToken = jwtUtils.generateRefreshToken(authRequest.getEmail());
            AuthResponse authResponse = new AuthResponse("Đăng nhập thành công", token, refreshToken);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/user/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(aesUtil.encrypt(authRequest.getEmail()), authRequest.getPassword())
        );
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            String token = jwtUtils.generateToken(aesUtil.encrypt(authRequest.getEmail()));
            String refreshToken = jwtUtils.generateRefreshToken(aesUtil.encrypt(authRequest.getEmail()));
            AuthResponse authResponse = new AuthResponse("Đăng nhập thành công", token, refreshToken);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserRequest request) {
        UserResponse result = userService.signUp1(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        String email = jwtUtils.extractAllClaimsRefresh(refreshToken).getSubject();
        boolean isValid = jwtUtils.isValidRefreshToken(refreshToken);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AuthResponse>builder()
                            .message("Refresh token failed")
                            .build()
                    );
        } else {
            String token = jwtUtils.generateToken(email);
            String newRefreshToken = jwtUtils.generateRefreshToken(email);
            AuthResponse authResponse = new AuthResponse("Refresh token success", token, newRefreshToken);
            return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                    .message("Refresh token success")
                    .result(authResponse)
                    .build()
            );

        }
    }
}
