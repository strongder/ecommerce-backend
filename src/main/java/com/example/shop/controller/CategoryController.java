package com.example.shop.controller;

import com.example.shop.dtos.request.CategoryRequest;
import com.example.shop.dtos.response.CategoryResponse;
import com.example.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAll()
	{
		List<CategoryResponse> result = categoryService.getAll();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable("id") Long id) {
        CategoryResponse categoryDTO = categoryService.getById(id);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }
    @PostMapping()
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        CategoryResponse result = categoryService.create(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
