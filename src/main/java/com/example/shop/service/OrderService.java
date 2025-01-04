package com.example.shop.service;


import com.example.shop.convert.OrderConvert;
import com.example.shop.dtos.OrderStatusType;
import com.example.shop.dtos.PaymentStatusType;
import com.example.shop.dtos.request.OrderRequest;
import com.example.shop.dtos.response.OrderResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.*;
import com.example.shop.repository.*;
import com.example.shop.utils.PaginationSortingUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;


@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@EnableScheduling
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    OrderRepository orderRepository;
    VarProductRepository varProductRepository;
    OrderConvert orderConvert;
    UserRepository userRepository;
    ProductService productService;
    CartService cartService;
    NotificationService notificationService;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;


    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest, Double total) {
        Order order = orderConvert.orderConvertToEntity(orderRequest);
        if (order.getUser().getCart().getNumberProduct() == 0) {
            throw new AppException(ErrorResponse.CART_EMPTY);
        }
        order.setTotal(total);
        order.setOrderCode(generateOrderCode(3));
        order.setCreatedAt(LocalDateTime.now());
        if (order.getPaymentMethod().equals("COD")) {
            order.setStatus(OrderStatusType.PENDING);
            orderRepository.save(order);

        } else if (order.getPaymentMethod().equals("VNPAY")) {
            order.setStatus(OrderStatusType.PENDING_PAYMENT);
            orderRepository.save(order);
            String title = "Hoàn tất thanh toán";
            String message = "Bạn có đơn hàng trị giá: " + formatPrice(order.getTotal()) + " vui lòng thanh toán trước:"
                    + formatTime(LocalDateTime.now().plusHours(24)) + " để hoàn tất đơn hàng. Vui lòng bỏ qua tin nhắn này nếu bạn đã thanh toán.";
            notificationService.sendNotificationToUser(order, title, message);
        }
        String title = "Đơn hàng mới";
        String message = "Bạn có đơn hàng mới: " + order.getOrderCode() + " vui lòng kiểm tra đơn hàng!";
        notificationService.sendOrderNotification(order, title, message);
        cartService.clearCart(order.getUser().getCart().getId());

        OrderResponse orderResponse = orderConvert.orderConvertToDTO(order);
        return orderResponse;
    }


    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            int pageNum, int pageSize, String sortDir, String sortBy) {
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
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
    public OrderResponse acceptOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        if (order.getStatus() == OrderStatusType.CANCELLED)
            throw new AppException(ErrorResponse.ORDER_CANCEL);
        order.setStatus(OrderStatusType.PROCESSING);
        orderRepository.save(order);
        String title = "Xử lý đơn hàng";
        String message = "Đơn hàng" + order.getOrderCode() + " đã được xử lý";
        notificationService.sendNotificationToUser(order, title, message);
        OrderResponse response = orderConvert.orderConvertToDTO(order);
        return response;
    }

    @Transactional
    public OrderResponse shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));

        if (order.getStatus() == OrderStatusType.CANCELLED)
            throw new AppException(ErrorResponse.ORDER_CANCEL);
        order.setStatus(OrderStatusType.SHIPED);
        orderRepository.save(order);
        String title = "Đơn hàng đã gửi";
        String message = "Đơn hàng: " + order.getOrderCode() + "của bạn đã được gửi đi.";
        notificationService.sendNotificationToUser(order, title, message);
        return orderConvert.orderConvertToDTO(order);
    }

    @Transactional
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        if (order.getStatus() == OrderStatusType.CANCELLED)
            throw new AppException(ErrorResponse.ORDER_CANCEL);
        order.setStatus(OrderStatusType.COMPLETED);
        orderRepository.save(order);
        String title = "Hoàn thành đơn hàng";
        String message = "Đơn hàng: " + order.getOrderCode() + " đã hoàn thành vui lòng đánh giá sản phẩm.";
        notificationService.sendNotificationToUser(order, title, message);
        return orderConvert.orderConvertToDTO(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED));
        if (order.getStatus() == OrderStatusType.PENDING || order.getStatus() == OrderStatusType.PENDING_PAYMENT) {
            order.setStatus(OrderStatusType.CANCELLED);
            order.getOrderItems().forEach(
                    orderItem -> {
                        VarProduct varProduct = orderItem.getVarProduct();
                        varProduct.setStock(varProduct.getStock() + orderItem.getQuantity());
                        varProduct.setQuantitySold(varProduct.getQuantitySold() - orderItem.getQuantity());
                        Product product = productRepository.findByVarProductsId(varProduct.getId());
                        product.setStock(product.getStock() + orderItem.getQuantity());
                        product.setQuantitySold(product.getQuantitySold() - orderItem.getQuantity());
                        varProductRepository.save(varProduct);
                    }
            );
            String title = "Hủy đơn hàng";
            String message = "Đơn hàng: " + order.getOrderCode() + " đã bị hủy";
            notificationService.sendOrderNotification(order, title, message);
            return orderConvert.orderConvertToDTO(order);
        } else
            throw new AppException(ErrorResponse.ORDER_SHIPED);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(Long userId, int pageNum, int pageSize, String sortDir, String sortBy) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorResponse.USER_NOT_EXISTED));
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(order -> orderConvert.orderConvertToDTO(order));
    }

    @Transactional
    @Scheduled(fixedRate = 86400000) // Kiểm tra mỗi giờ (3600000 milliseconds = 1 giờ)
    public void checkPendingOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatusType.PENDING_PAYMENT);
        log.info("Checking pending orders: {}", pendingOrders.size());
        List<Payment> payments = paymentRepository.findByOrderIn(pendingOrders);
        payments.forEach(
                payment ->
                {
                    log.info("Checking payment: {}", payment.getId());
                    if (payment.getStatus() == PaymentStatusType.PENDING && Duration.between(payment.getPaymentTime(), now).toHours() >= 24) {
                        payment.setStatus(PaymentStatusType.EXPIRED);
                        paymentRepository.save(payment);
                        cancelOrder(payment.getOrder().getId());
                    }
                }
        );
    }

    public OrderResponse updatePaymentMethod(Long orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(ErrorResponse.ORDER_NOT_EXISTED)
        );
        order.setPaymentMethod(paymentMethod.toUpperCase());
        orderRepository.save(order);
        return orderConvert.orderConvertToDTO(order);
    }


    public static String generateOrderCode(int length) {
        // lay thoi gian theo
        Long currentTime = System.currentTimeMillis();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder orderCode = new StringBuilder(currentTime.toString().substring(5));
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            orderCode.append(characters.charAt(index));
        }

        return orderCode.toString();
    }

    public static String formatPrice(Double price) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return String.valueOf(decimalFormat.format(price));

    }

    public static String formatTime(LocalDateTime time) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.valueOf(time.format(dateTimeFormat));

    }
}
