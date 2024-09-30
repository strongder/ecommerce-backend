package com.example.shop.service;

import com.example.shop.convert.UserConvert;
import com.example.shop.dtos.request.UserRequest;
import com.example.shop.dtos.response.UserResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Cart;
import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.utils.PaginationSortingUtils;
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
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService{

	 UserRepository userRepository;
	 PasswordEncoder passwordEncoder;
	 UserConvert userConvert;


	@Transactional
	public UserResponse create(UserRequest request) {
		Optional<User> existedUser = userRepository.findByUsername(request.getUsername());
		if(existedUser.isPresent())
		{
			throw new AppException(ErrorResponse.USER_EXISTED);
		}
		else {
			User user = userConvert.convertToEntity(request);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
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
		if(existedUser.isPresent())
		{
			User user = existedUser.get();
			//user.setUsername(request.getUsername());
			user.setFullName(request.getFullName());
			user.setAvatar(request.getAvatar());
			user.setPhone(request.getPhone());
			user.setEmail(request.getEmail());
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			return userConvert.convertToDTO(user);
		}
		throw new AppException(ErrorResponse.USER_NOT_EXISTED);
	}

	@Transactional(readOnly = true)
	public UserResponse getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByEmail(authentication.getName()).orElse(null);
		return  userConvert.convertToDTO(user);
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
		if(user.isPresent())
		{
			return userConvert.convertToDTO(user.get());
		}
		throw new AppException(ErrorResponse.USER_NOT_EXISTED);

	}

	@Transactional
	public UserResponse signUp(UserRequest request) {
		Optional<User> existedUser = userRepository.findByUsername(request.getUsername());
		if(existedUser.isPresent())
		{
			throw new AppException(ErrorResponse.USER_EXISTED);
		}
		else {

			Set<String> roles = new HashSet<>();
			roles.add("USER");
			request.setRoles(roles);
			User user = userConvert.convertToEntity(request);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			Cart cart = new Cart();
			cart.setUser(user);
			user.setCart(cart);
			userRepository.save(user);
			return userConvert.convertToDTO(user);
		}
	}



}