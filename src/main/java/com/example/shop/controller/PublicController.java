package com.example.shop.controller;


import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.CategoryResponse;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.dtos.response.ProductSaleDTO;
import com.example.shop.repository.CategoryRepository;
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
    CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getAll() {
        List<CategoryResponse> result = categoryService.getAll();
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Get all categories success")
                .result(result)
                .build();
    }

    @GetMapping("/categories/parent")
    public ApiResponse<List<CategoryResponse>> getParentCategories() {
        List<CategoryResponse> result = categoryService.fetchParentCategory();
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Get all parent categories success")
                .result(result)
                .build();
    }

//    @GetMapping("/categories/child")
//    public ApiResponse<List<CategoryResponse>> getChildCategories() {
//        List<CategoryResponse> result = categoryService.fetchChildCategory();
//        return ApiResponse.<List<CategoryResponse>>builder()
//                .message("Get all child categories success")
//                .result(result)
//                .build();
//    }

    @GetMapping("/categories/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable("id") Long id) {
        CategoryResponse category = categoryService.getById(id);
        return ApiResponse.<CategoryResponse>builder()
                .message("Get category by id success")
                .result(category)
                .build();
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id) {
        ProductResponse product = productService.getById(id);
        return ApiResponse.<ProductResponse>builder()
                .message("Get product by id success")
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
            @RequestParam(value = "pageSize", defaultValue = "30") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy) {

        Page<ProductResponse> result = productService.getProductsByCategory(categoryId, pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<ProductResponse>>builder()
                .message("Get product by category success")
                .result(result)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> searchProduct(
            @RequestParam(value = "query", required = true) String query,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "rating", defaultValue = "0") Integer rating,
            @RequestParam(value = "price", defaultValue = "0") Double price,
            @RequestParam(value = "discount", defaultValue = "false") Boolean discount) {
        Page<ProductResponse> result = productService.getProductByKey(query, pageNum, pageSize, sortDir, sortBy, rating, price, discount);
        return ApiResponse.<Page<ProductResponse>>builder()
                .message("Search product success")
                .result(result)
                .build();
    }

    @GetMapping("/product-discount")
    public ApiResponse<Page<ProductResponse>> findByDiscount(
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy) {
        Page<ProductResponse> result = productService.getProductDiscount(pageNum, pageSize, sortDir, sortBy);
        return ApiResponse.<Page<ProductResponse>>builder()
                .message("Search product success")
                .result(result)
                .build();
    }

    //    @GetMapping("/subCategory")
//    public ApiResponse<List<Category>> fetchSubCategories() {
//        List<Category> result = categoryRepository.findBySubCategories();
//        return ApiResponse.<List<Category>>builder()
//                .message("Fetch parent categories success")
//                .result(result)
//                .build();
//    }
    @GetMapping("/top-selling")
    public ApiResponse<List<ProductSaleDTO>> getTopSellingProductsByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<ProductSaleDTO> result = productService.getTopSellingProductsByCategoryAndDate(categoryId, startDate, endDate, limit);
        return ApiResponse.<List<ProductSaleDTO>>builder()
                .message("Get top selling products success")
                .result(result)
                .build();
    }

}
