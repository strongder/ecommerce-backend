package com.example.shop.service;


import com.example.shop.convert.OrderConvert;
import com.example.shop.dtos.request.OrderRequest;
import com.example.shop.dtos.response.OrderResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Order;
import com.example.shop.model.User;
import com.example.shop.model.VarProduct;
import com.example.shop.repository.*;
import com.example.shop.utils.PaginationSortingUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    OrderRepository orderRepository;
    VarProductRepository varProductRepository;
    OrderConvert orderConvert;
    ProductRepository productRepository;
    UserRepository userRepository;
    CartService cartService;
    AddressRepository addressRepository;
    NotificationService notificationService;


    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = orderConvert.orderConvertToEntity(orderRequest);
        if (order.getUser().getCart().getNumberProduct() == 0) {
            throw new AppException(ErrorResponse.CART_EMPTY);
        }
        cartService.clearCart(order.getUser().getCart().getId());
        if (order.getPaymentMethod().equals("COD")) {
            order.setStatus("PENDING");
            orderRepository.save(order);
        } else if (order.getPaymentMethod().equals("VNPAY")) {
            order.setStatus("PENDING PAYMENT");
            orderRepository.save(order);
        }
        order.setOrderCode(generateOrderCode(6));
        String title = "New Order";
        String message = "You have a new order with order code: " + order.getOrderCode();
        notificationService.sendOrderNotification(order.getId(), title, message);
        OrderResponse orderResponse = orderConvert.orderConvertToDTO(order);
        return orderResponse;
    }


    @Scheduled(fixedRate = 3600000) // Kiểm tra mỗi giờ (3600000 milliseconds = 1 giờ)
    public void checkPendingOrders() {
        Date now = new Date();
        List<Order> pendingOrders = orderRepository.findByStatus("PENDING PAYMENT");

        for (Order order : pendingOrders) {
            long diffInMillies = Math.abs(now.getTime() - order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            long diffInHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diffInHours >= 24) { // Giả sử đơn hàng hết hạn sau 24 giờ
                cancelOrder(order.getId());
                orderRepository.save(order);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            int pageNum, int pageSize, String sortDir, String sortBy) {
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum  , pageSize, sortDir, sortBy);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(order -> orderConvert.orderConvertToDTO(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        return orderConvert.orderConvertToDTO(order);
    }


    @Transactional
    public OrderResponse acceptOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        if(order.getStatus().equals("PAID"))
            throw new AppException(ErrorResponse.ORDER_PAID );
        order.setStatus("ACCEPTED");
        orderRepository.save(order);
        return orderConvert.orderConvertToDTO(order);
    }

    @Transactional
    public OrderResponse shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));

        if(order.getStatus().equals("PAID"))
            throw new AppException(ErrorResponse.ORDER_PAID );
        order.setStatus("SHIPPED");
        orderRepository.save(order);
        String title = "Order Shipped";
        String message = "Your order with order code: " + order.getOrderCode() + " has been shipped";
        notificationService.sendNotificationToUser(order.getUser(), title, message);
        return orderConvert.orderConvertToDTO(order);
    }
    @Transactional
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        if(order.getStatus().equals("PAID"))
            throw new AppException(ErrorResponse.ORDER_PAID );
        order.setStatus("COMPLETED");
        orderRepository.save(order);
        String title = "Order Shipped";
        String message = "Your order with order code: " + order.getOrderCode() + " has been shipped";
        notificationService.sendNotificationToUser(order.getUser(), title, message);
        return orderConvert.orderConvertToDTO(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        if(order.getStatus().equals("PAID"))
            throw new AppException(ErrorResponse.ORDER_PAID );
        order.setStatus("CANCELLED");
        order.getOrderItems().forEach(
                orderItem -> {
                    VarProduct varProduct = orderItem.getVarProduct();
                    varProduct.setStock(varProduct.getStock() + orderItem.getQuantity());
                    varProductRepository.save(varProduct);
                }
        );
        String title = "Order cancelled";
        String message = "The order with order code: " + order.getOrderCode() + " has been cancelled";
        notificationService.sendOrderNotification(order.getId(), title, message);
        return orderConvert.orderConvertToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(Long userId, int pageNum, int pageSize, String sortDir, String sortBy) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorResponse.USER_NOT_EXISTED));
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(order -> orderConvert.orderConvertToDTO(order));
    }

    public static String generateOrderCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder orderCode = new StringBuilder("2048");
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            orderCode.append(characters.charAt(index));
        }

        return orderCode.toString();
    }
}
