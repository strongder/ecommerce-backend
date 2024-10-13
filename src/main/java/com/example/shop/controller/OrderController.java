package com.example.shop.controller;

import com.example.shop.dtos.request.OrderRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.OrderResponse;
import com.example.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/orders")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public ApiResponse<OrderResponse> placeOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestParam("total") Double total
    ) {
        OrderResponse result = orderService.placeOrder(orderRequest, total);

        return ApiResponse.<OrderResponse>builder()
                .message("Place order success")
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(
            @PathVariable Long id
    ) {
        OrderResponse result = orderService.getOrderById(id);

        return  ApiResponse.<OrderResponse>builder()
                .message("Get order by id success")
                .result(result)
                .build();
    }

    @GetMapping("")
    public ApiResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy
    ) {
        Page<OrderResponse> orderDTO = orderService.getAllOrders(pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<OrderResponse>>builder()
                .message("Get all orders success")
                .result(orderDTO)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Page<OrderResponse>> getOrderByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "40") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy
    ) {
        Page<OrderResponse> result = orderService.getAllOrders(pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<OrderResponse>>builder()
                .message("Get all orders success")
                .result(result)
                .build();
    }

    @PutMapping("/cancel-order/{id}")
    public ApiResponse<OrderResponse> cancelOrder(
            @PathVariable("id") Long id
    ) {
        OrderResponse result = orderService.cancelOrder(id);
        return ApiResponse.<OrderResponse>builder()
                .message("Cancel order success")
                .result(result)
                .build();
    }

    @PutMapping("/ship-order/{id}")
    public ApiResponse<OrderResponse> shipedOrddr(
            @PathVariable("id") Long id
    ) {
        OrderResponse result = orderService.shipOrder(id);
        return ApiResponse.<OrderResponse>builder()
                .message("ship order success")
                .result(result)
                .build();
    }
    @PutMapping("/complete-order/{id}")
    public ApiResponse<OrderResponse> completeOrddr(
            @PathVariable("id") Long id
    ) {
        OrderResponse result = orderService.completeOrder(id);
        return ApiResponse.<OrderResponse>builder()
                .message("Cancel order success")
                .result(result)
                .build();
    }

    @PutMapping("/accept-order/{id}")
    public ApiResponse<OrderResponse> acceptOrder(
            @PathVariable("id") Long id
    ) {
        OrderResponse result = orderService.acceptOrder(id);
        return ApiResponse.<OrderResponse>builder()
                .message("Accept order success")
                .result(result)
                .build();
    }

    @PutMapping("/update-payment-method")
    public ApiResponse<OrderResponse> updatePaymentMethod(
            @RequestParam Long orderId,
            @RequestParam String paymentMethod
    ){
        var result = orderService.updatePaymentMethod(orderId, paymentMethod);
        return ApiResponse.<OrderResponse>builder()
                .message("update order success")
                .result(result)
                .build();
    }

}
