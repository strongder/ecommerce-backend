package com.example.shop.controller;


import com.example.shop.dtos.request.ReviewRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.ReviewResponse;
import com.example.shop.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReviewController {

    ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> creatReview(@RequestBody ReviewRequest request){
        return ApiResponse.<ReviewResponse>builder()
                .message("Create review success")
                .result(reviewService.createReview(request))
                .build();
    }


    @GetMapping("/product/{productId}")
    public ApiResponse<Page<ReviewResponse>> getReviewByProductId(@PathVariable  Long productId
            , @RequestParam(value = "pageNum", defaultValue = "0") int pageNum
            , @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
            , @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
            , @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy){

        Page<ReviewResponse> result = reviewService.getReviewByProductId(productId, pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<ReviewResponse>>builder()
                .message("Get review by product id success")
                .result(reviewService.getReviewByProductId(productId, pageNum, pageSize, sortDir, sortBy))
                .build();
    }
}
