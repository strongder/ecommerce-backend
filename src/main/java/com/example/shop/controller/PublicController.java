package com.example.shop.controller;


import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublicController {

    CategoryService categoryService;
    ProductService productService;

//    @GetMapping("/categories")
//    public ApiResponse<Page<CategoryResponse>> getAll(
//            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
//            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
//            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
//            @RequestParam(value = "sortBy", defaultValue = "username") String sortBy
//    )
//    {
//        Page<CategoryResponse> result = categoryService.getAll(pageNum, pageSize, sortDir, sortBy);
//        return ApiResponse.<Page<CategoryResponse>>builder()
//                .message("Get all categories success")
//                .result(result)
//                .build();
//    }


    @GetMapping("/products/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable("id") Long id) {
        ProductResponse product = productService.getById(id);
        return  ApiResponse.<ProductResponse>builder()
                .message("Get product by id success")
                .result(product)
                .build();
    }

    @GetMapping("/search/{key}")
    public ApiResponse<List<ProductResponse>> getProductByKey(@PathVariable("key") String key) {
        List<ProductResponse> product = productService.getProductByKey(key);
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Get product by key success")
                .result(product)
                .build();
    }

    @GetMapping("/products")
    public ApiResponse<Page<ProductResponse>> getAll(
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy) {
        Page<ProductResponse> productPage = productService.getAll(pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<ProductResponse>>builder()
                .message("Get all product success")
                .result(productPage)
                .build();
    }

    @GetMapping("/products/category/{categoryId}")
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
}
