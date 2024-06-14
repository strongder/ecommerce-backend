package com.example.shop.controller;

import com.example.shop.dtos.request.CartLineItemRequest;
import com.example.shop.dtos.response.CartResponse;
import com.example.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/carts")
@RestController
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping("/{cartId}")
	public ResponseEntity<CartResponse> addProductByCart(
			@PathVariable("cartId") Long cartId,
			@RequestBody CartLineItemRequest request
			)
	{
		CartResponse result = cartService.addProductToCart(cartId, request);
		return new ResponseEntity<>(
				result, HttpStatus.CREATED);
	}

	@GetMapping("/{cartId}")
	public ResponseEntity<CartResponse> getProductByCart(
			@PathVariable("cartId") Long cartId)
	{
		CartResponse result = cartService.getById(cartId);
		return new ResponseEntity<>(
				result, HttpStatus.OK);
	}

}
