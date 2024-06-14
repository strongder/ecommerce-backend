package com.example.shop.service;

import com.example.shop.dtos.request.CartLineItemRequest;
import com.example.shop.dtos.response.CartResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Cart;
import com.example.shop.model.CartLineItem;
import com.example.shop.model.VariantProduct;
import com.example.shop.repository.CartLineItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.VariantProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class CartService{

	 CartRepository cartRepository;
	 VariantProductRepository variantProductRepository;
	 CartLineItemRepository cartLineItemRepository;
	 ModelMapper modelMapper;

	@Transactional
	public CartResponse addProductToCart(Long cartId, CartLineItemRequest request) {

		VariantProduct variantProduct = variantProductRepository.findById(request.getVariantProductId())
				.orElseThrow(() -> new AppException(ErrorResponse.PRODUCT_NOT_EXISTED));

		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new AppException(ErrorResponse.USER_NOT_EXISTED));

		CartLineItem cartLineItem = modelMapper.map(request, CartLineItem.class);

		CartLineItem existingCartLineItem = cartLineItemRepository.findByVariantProductId(cartLineItem.getVariantProduct().getId());

		if (existingCartLineItem != null && !existingCartLineItem.isDelete()) {
			existingCartLineItem.setAmount(existingCartLineItem.getAmount() + cartLineItem.getAmount());
			existingCartLineItem.setPrice(existingCartLineItem.getAmount()*variantProduct.getPrice());
			cartLineItemRepository.save(existingCartLineItem);
			cart.getCartLineItems().add(existingCartLineItem);
		} else {
			cartLineItem.setVariantProduct(variantProduct);
			cartLineItem.setPrice(variantProduct.getPrice() * cartLineItem.getAmount());
			cartLineItem.setCart(cart);
			cartLineItemRepository.save(cartLineItem);
			cart.getCartLineItems().add(cartLineItem);
		}

		// Tính tổng giá trị đơn hàng
		Double total = calculateTotalPrice(cart.getCartLineItems());
		cart.setTotal(total);
		cart.setNumberProduct(cartLineItemRepository.numberProduct(cartId));
		cartRepository.save(cart);

		return modelMapper.map(cart, CartResponse.class);
	}

	private Double calculateTotalPrice(List<CartLineItem> cartLineItems) {
		return cartLineItems.stream()
				.filter(cartLineItem -> !cartLineItem.isDelete())
				.mapToDouble(CartLineItem::getPrice)
				.sum();
	}


	private Double calculateTotalPrice(Set<CartLineItem> cartLineItems) {
		Double totalPrice = 0.0;
		for (CartLineItem cartItem : cartLineItems) {
			if (cartItem.isDelete() == false) {
				totalPrice += cartItem.getPrice();
			}
		}
		return totalPrice;
	}


	public void clearCart(Long id) {
		Cart cart = cartRepository.findById(id).orElse(null);
		Set<CartLineItem> list = cart.getCartLineItems();
		for (CartLineItem cartItem : list) {
			cartItem.setDelete(true);
			VariantProduct variantProduct = cartItem.getVariantProduct();
			variantProduct.setAmount(variantProduct.getAmount()-cartItem.getAmount());
			variantProductRepository.save(variantProduct);
			cartLineItemRepository.save(cartItem);
		}
		cart.setTotal(0.0);
		cart.setNumberProduct(0);
		cartRepository.save(cart);
	}

	public  CartResponse getById(Long cartId)
	{
		Cart cart = cartRepository.findById(cartId).orElse(null);
		Set<CartLineItem> cartItems = cartLineItemRepository.findByCart(cartId);
		cart.setCartLineItems(cartItems);
		return modelMapper.map(cart, CartResponse.class);
	}
}
