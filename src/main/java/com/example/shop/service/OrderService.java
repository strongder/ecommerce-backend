package com.example.shop.service;


import com.example.shop.dtos.request.OrderRequest;
import com.example.shop.dtos.response.AddressResponse;
import com.example.shop.dtos.response.CartLineItemResponse;
import com.example.shop.dtos.response.OrderResponse;
import com.example.shop.model.*;
import com.example.shop.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class OrderService {


	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartService cartService;
    @Autowired
    private CartLineItemRepository cartLineItemRepository;
	@Autowired
    AddressRepository addressRepository;


	@Transactional
	public OrderResponse placeOrder(OrderRequest orderRequest)
	{
		User user = userRepository.findById(orderRequest.getUserId()).orElse(null) ;
		Cart cart = user.getCart();
		Address address = addressRepository.findById(orderRequest.getAddressId()).orElse(null);
		Order order = new Order();
		order.setAddress(address);
        order.setReceiveTime(orderRequest.getReceiveTime());
		order.setDeliveryTime(orderRequest.getDeliveryTime());
		order.setUser(user);
		order.setTotal(cart.getTotal());
		order.setStatus(orderRequest.getStatus());
		orderRepository.save(order);

		OrderResponse orderResponse = new OrderResponse();
		orderResponse.setRecipientName(order.getUser().getFullName());
		orderResponse.setAddress(modelMapper.map(address, AddressResponse.class));
		orderResponse.setCreatedAt(order.getCreatedAt());
        orderResponse.setDeliveryTime(order.getDeliveryTime());
		orderResponse.setDeliveryTime(order.getDeliveryTime());
		orderResponse.setTotal(order.getTotal());

		Set<CartLineItem> list = new HashSet<>();
		for(CartLineItem c: order.getUser().getCart().getCartLineItems())
		{
			if (c.isDelete()==false) {
				list.add(c);
			}
		}
		orderResponse.setCartLineItems(list.stream().map(c -> modelMapper.map(c, CartLineItemResponse.class)).collect(Collectors.toSet()));
		orderResponse.setStatus(order.getStatus());
		cartService.clearCart(cart.getId());
		return orderResponse;
	}

}
