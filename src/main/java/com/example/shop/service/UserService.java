package com.example.shop.service;

import com.example.shop.convert.UserConvert;
import com.example.shop.dtos.request.UserRequest;
import com.example.shop.dtos.response.UserResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Cart;
import com.example.shop.model.User;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.utils.PaginationSortingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserConvert userConvert;
    private final ProductService productService;
    private final ProductRepository productRepository;


    @Transactional
    public UserResponse create(UserRequest request) {
        Optional<User> existedUser = userRepository.findByEmail(request.getEmail());
        if (existedUser.isPresent()) {
            throw new AppException(ErrorResponse.USER_EXISTED);
        } else {
            User user = userConvert.convertToEntity(request);
            user.setUsername(user.getUsername() + generateSuffixUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setAvatar("https://i.pinimg.com/736x/c6/e5/65/c6e56503cfdd87da299f72dc416023d4.jpg");
            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
            userRepository.save(user);
            return userConvert.convertToDTO(user);
        }
    }

    @Transactional
    public UserResponse update(Long userId, UserRequest request) {
        Optional<User> existedUser = userRepository.findById(userId);
        if (existedUser.isPresent()) {
            User user = existedUser.get();
            user.setUsername(request.getUsername());
            user.setFullName(request.getFullName());
            user.setAvatar(request.getAvatar());
            user.setPhone(request.getPhone());
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            userRepository.save(user);
            return userConvert.convertToDTO(user);
        }
        throw new AppException(ErrorResponse.USER_NOT_EXISTED);
    }

    public String updateAvatar(Map<String, String> avatarObject, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            var avatar = avatarObject.get("avatar");
            user.get().setAvatar(avatar);
            userRepository.save(user.get());
            return avatar;
        }
        throw new AppException(ErrorResponse.USER_NOT_EXISTED);
    }


    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        return userConvert.convertToDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(int pageNum, int pageSize, String sortDir, String sortBy
    ) {
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> userConvert.convertToDTO(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return userConvert.convertToDTO(user.get());
        }
        throw new AppException(ErrorResponse.USER_NOT_EXISTED);

    }

    @Transactional
    public UserResponse signUp(UserRequest request) {
        Optional<User> existedUser = userRepository.findByEmail(request.getEmail());
        if (existedUser.isPresent()) {
            throw new AppException(ErrorResponse.USER_EXISTED);
        } else {

            Set<String> roles = new HashSet<>();
            roles.add("USER");
            request.setRoles(roles);
            User user = userConvert.convertToEntity(request);
            user.setAvatar("https://i.pinimg.com/736x/c6/e5/65/c6e56503cfdd87da299f72dc416023d4.jpg");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUsername(user.getUsername() + generateSuffixUsername());
            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
            userRepository.save(user);
            return userConvert.convertToDTO(user);
        }
    }


    public String generateSuffixUsername() {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder("#");
        int count = 4;
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


}