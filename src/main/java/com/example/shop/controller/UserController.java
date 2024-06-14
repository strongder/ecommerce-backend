package com.example.shop.controller;


import com.example.shop.dtos.request.UserRequest;
import com.example.shop.dtos.response.UserResponse;
import com.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/current-user")
	public ResponseEntity<UserResponse> getCurrentUser()
	{
		UserResponse result = userService.getCurrentUser();
		return new ResponseEntity<>(result,HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<List<UserResponse>> getAllUser()
	{
		List<UserResponse> result = userService.getAll();
		return new ResponseEntity<>(result,HttpStatus.OK);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<UserResponse> update(@PathVariable("userId") Long userId, @RequestBody UserRequest request)
	{
		UserResponse result= userService.update(userId,request);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register")
	public ResponseEntity<UserResponse> create(@RequestBody UserRequest request)
	{
		UserResponse result= userService.create(request);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

}
