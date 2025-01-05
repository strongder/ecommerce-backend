package com.example.shop.controller;


import com.example.shop.dtos.request.UserRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.UserResponse;
import com.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/current-user")
	public ApiResponse<UserResponse> getCurrentUser()
	{
		UserResponse result = userService.getCurrentUser();
		return  ApiResponse.<UserResponse>builder()
				.message("Get current user success")
				.result(result)
				.build();
	}

	@GetMapping()
	public ApiResponse<Page<UserResponse>> getAllUser(
			@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
			@RequestParam(value = "sortBy", defaultValue = "username") String sortBy
	)
	{
		Page<UserResponse> result = userService.getAll1(pageNum, pageSize, sortDir, sortBy);
		return  ApiResponse.<Page<UserResponse>>builder()
				.message("Get all users success")
				.result(result)
				.build();
	}
	@GetMapping("/{userId}")
	public ApiResponse<UserResponse> getById(@PathVariable("userId") Long userId)
	{
		UserResponse result = userService.getById(userId);
		return  ApiResponse.<UserResponse>builder()
				.message("Get user by id success")
				.result(result)
				.build();
	}
	@PutMapping("/update/{userId}")
	public ApiResponse<UserResponse> update(@PathVariable("userId") Long userId, @RequestBody UserRequest request)
	{
		UserResponse result= userService.update1(userId,request);
		return  ApiResponse.<UserResponse>builder()
				.message("Update user success")
				.result(result)
				.build();
	}
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register")
	public ApiResponse<UserResponse> create(@RequestBody UserRequest request)
	{
		UserResponse result= userService.create(request);
		return  ApiResponse.<UserResponse>builder()
				.message("Create user success")
				.result(result)
				.build();
	}

	@PutMapping("/{userId}/update-avatar")
	public ApiResponse<String> updateAvatar(
			@PathVariable("userId") Long userId,
			@RequestBody Map<String, String> avatar) {

			String updatedAvatar = userService.updateAvatar(avatar, userId);
			return ApiResponse.<String>builder()
					.message("Update avatar success")
					.result(updatedAvatar)
					.build();

	}

}
