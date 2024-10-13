package com.example.shop.controller;

import com.example.shop.dtos.request.PaymentRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.PaymentResponse;
import com.example.shop.dtos.response.VnpayResponse;
import com.example.shop.repository.PaymentRepository;
import com.example.shop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    PaymentRepository paymentRepository;

    @PostMapping("/vn-pay")
    public ApiResponse<VnpayResponse> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        return  ApiResponse.<VnpayResponse>builder()
                .result(paymentService.createVnPayPayment(paymentRequest, request))
                .message("Create payment successfully")
                .build();
    }

    @GetMapping("/vnpay-callback")
    public ApiResponse<VnpayResponse> payCallbackHandler(HttpServletRequest request) {
        // Lấy tham số từ yêu cầu
        String status = request.getParameter("vnp_ResponseCode");
        String transactionId = request.getParameter("vnp_TxnRef");

        // Gọi phương thức từ service để xử lý callback
        VnpayResponse vnpayResponse = paymentService.handlePaymentCallback(status, transactionId);
        String message = status.equals("00") ? "Payment successfully" : "Payment failed";
        return ApiResponse.<VnpayResponse>builder()
                .result(vnpayResponse)
                .message(message).build();
    }


    @GetMapping()
    public ApiResponse<Page<PaymentResponse>> getAllPayment(
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "paymentTime") String sortBy
    ) {
        return ApiResponse.<Page<PaymentResponse>>builder()
                .message("Get all payment success")
                .result(paymentService.getAll(pageNum, pageSize, sortDir, sortBy))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PaymentResponse>> getPaymentByUserId(
            @PathVariable Long userId

    ) {
        return ApiResponse.<List<PaymentResponse>>builder()
                .message("Get all payment success")
                .result(paymentService.getPaymentByUser(userId))
                .build();
    }
}
