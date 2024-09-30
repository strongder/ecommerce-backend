package com.example.shop.controller;

import com.example.shop.dtos.request.CategoryRequest;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.CategoryResponse;
import com.example.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public ApiResponse<Page<CategoryResponse>> getAll(
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "sortBy", defaultValue = "createAt") String sortBy
    )
	{
		Page<CategoryResponse> result = categoryService.getAll(pageNum, pageSize, sortDir, sortBy);
		return ApiResponse.<Page<CategoryResponse>>builder()
                .message("Get all categories success")
                .result(result)
                .build();
	}
	@GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable("id") Long id) {
        CategoryResponse result = categoryService.getById(id);
        return  ApiResponse.<CategoryResponse>builder()
                .message("Get category by id success")
                .result(result)
                .build();
    }
    @PostMapping()
    public ApiResponse<CategoryResponse> create(@RequestBody CategoryRequest request) {
        CategoryResponse result = categoryService.create(request);
        return ApiResponse.<CategoryResponse>builder()
                .message("Create category success")
                .result(result)
                .build();
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> update(@PathVariable Long categoryId, @RequestBody CategoryRequest request) {
        CategoryResponse result = categoryService.update(categoryId,request);
        return ApiResponse.<CategoryResponse>builder()
                .message("Update category success")
                .result(result)
                .build();
    }

}
