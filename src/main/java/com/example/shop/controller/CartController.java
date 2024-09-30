 package com.example.shop.controller;

 import com.example.shop.dtos.request.CartItemRequest;
 import com.example.shop.dtos.response.ApiResponse;
 import com.example.shop.dtos.response.CartResponse;
 import com.example.shop.service.CartService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/v1/carts")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping("/{userId}")
	public ApiResponse<CartResponse> addProductByCart(
			@PathVariable("userId") Long userId,
			@RequestBody CartItemRequest request
			)
	{
		CartResponse result = cartService.addProductToCart(userId, request);
		return ApiResponse.<CartResponse>builder()
				.message("Add product to cart success")
				.result(result)
				.build();
	}

	@GetMapping("/{userId}")
	public ApiResponse<CartResponse> getProductByCart(
			@PathVariable("userId") Long userId)
	{
		CartResponse result = cartService.getByUserId(userId);
		return ApiResponse.<CartResponse>builder()
				.message("Get product by cart success")
				.result(result)
				.build();
	}

	@PutMapping("/remove-cart-item")
	public ApiResponse<CartResponse> removeCartItem(
			@RequestParam("userId") Long userId,
			@RequestParam("cartItemId") Long cartItemId
			)
	{
		CartResponse result = cartService.removeCartItem(userId, cartItemId);
		return ApiResponse.<CartResponse>builder()
				.message("Remove product from cart success")
				.result(result)
				.build();
	}

	@PutMapping("/update-cart-item")
	public ApiResponse<CartResponse> updateCartItem(
			@RequestParam("userId") Long userId,
			@RequestParam("cartItemId") Long cartItemId,
			@RequestParam("quantity") int quantity
			)
	{
		CartResponse result = cartService.updateProductInCart(userId, cartItemId, quantity);
		return ApiResponse.<CartResponse>builder()
				.message("Update product in cart success")
				.result(result)
				.build();
	}
}
