package com.example.shop.controller;


import com.example.shop.dtos.request.ProductRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.dtos.response.ProductSaleDTO;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable("id") Long id) {
        ProductResponse product = productService.getById(id);
        return  ApiResponse.<ProductResponse>builder()
                .message("Get product by id success")
                .result(product)
                .build();
    }


    @GetMapping()
    public ApiResponse<Page<ProductResponse>> getAll(
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy) {
        Page<ProductResponse> productPage = productService.getAll(pageNum, pageSize, sortDir, sortBy);
            return ApiResponse.<Page<ProductResponse>>builder()
                    .message("Get all product success")
                    .result(productPage)
                    .build();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy) {

        Page<ProductResponse> result = productService.getProductsByCategory(categoryId, pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<ProductResponse>>builder()
                .message("Get product by category success")
                .result(result)
                .build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ApiResponse<ProductResponse> create(@RequestBody ProductRequest request) {
        ProductResponse result = productService.create(request);
        return ApiResponse.<ProductResponse>builder()
                .message("Create product success")
                .result(result)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> update(
            @PathVariable("productId") Long productId,
            @RequestBody ProductRequest request) {
        ProductResponse result = productService.update(productId, request);
        return ApiResponse.<ProductResponse>builder()
                .message("Update product success")
                .result(result)
                .build();
    }
    @GetMapping("/top-selling")
    public ApiResponse<List<ProductSaleDTO>> getTopSellingProductsByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "limit", defaultValue = "5") int limit)
    {
        List<ProductSaleDTO> result = productService.getTopSellingProductsByCategoryAndDate(categoryId, startDate, endDate, limit);
        return ApiResponse.<List<ProductSaleDTO>>builder()
                .message("Get top selling products success")
                .result(result)
                .build();
    }

    @GetMapping("/statistics")
    public ApiResponse<List<ProductSaleDTO>> productStatistics(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum)
    {
        List<ProductSaleDTO> result = productService.getProductStatistics(startDate, endDate, pageNum);
        return ApiResponse.<List<ProductSaleDTO>>builder()
                .message("Get product statistics success")
                .result(result)
                .build();
    }

    @GetMapping("/total-sold")
    public ApiResponse<?> totalProductSold() {
        int result = productRepository.totalProductSold();
        return ApiResponse.builder()
                .message("Get total product sold success")
                .result(result)
                .build();
    }

    @GetMapping("/total-in-stock")
    public ApiResponse<?> totalProductInStock() {
        int result = productRepository.totalProductInStock()-productRepository.totalProductSold();
        return ApiResponse.builder()
                .message("Get total product in stock success")
                .result(result)
                .build();
    }
}
