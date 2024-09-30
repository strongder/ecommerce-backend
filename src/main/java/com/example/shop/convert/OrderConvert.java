package com.example.shop.convert;

import com.example.shop.dtos.request.OrderRequest;
import com.example.shop.dtos.response.OrderResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.*;
import com.example.shop.repository.AddressRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderConvert {

    ModelMapper modelMapper;
    UserRepository userRepository;
    ProductRepository productRepository;
    AddressRepository addressRepository;


    public Order orderConvertToEntity(OrderRequest request)
    {
        Order order = modelMapper.map(request, Order.class);
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                ()-> new AppException(ErrorResponse.USER_NOT_EXISTED));
        order.setUser(user);
        order.setOrderItems(convertCartItemToOrderItem(order.getUser().getCart().getCartItems(), order));
        order.setAddress(addressRepository.findById(request.getAddressId()).orElse(null));
        order.setTotal(user.getCart().getTotal());
        return order;
    }
    public OrderResponse orderConvertToDTO(Order order)
    {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setPaymentMethod(order.getPaymentMethod());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setTotal(order.getTotal());
        orderResponse.getOrderItems().forEach(
                orderItem -> {
                    Product product = productRepository.findByVarProductsId(orderItem.getVarProduct().getId());
                    orderItem.setName(product.getName());
                    orderItem.setImage(product.getImageUrls().get(0).getImageUrl());
                    orderItem.setPrice(product.getPrice());
                }
        );
        return orderResponse;
    }

    public Set<OrderItem> convertCartItemToOrderItem (Set<CartItem> cartItems, Order order)
    {
        Set<OrderItem> orderItems = new HashSet<>();
        for(CartItem cartItem : cartItems)
        {
            if(!cartItem.isDelete())
            {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setVarProduct(cartItem.getVarProduct());
                orderItem.setOrder(order);
                orderItems.add(orderItem);}
        }
        return orderItems;
    }


}
