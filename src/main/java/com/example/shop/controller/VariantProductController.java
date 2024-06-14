package com.example.shop.controller;

import com.example.shop.dtos.request.VariantProductRequest;
import com.example.shop.dtos.response.VariantProductResponse;
import com.example.shop.service.VariantProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variant-products")
public class VariantProductController {

	@Autowired
	private VariantProductService variantProductService;
	@GetMapping("/{id}")
	public ResponseEntity<VariantProductResponse> getById(@PathVariable("id") Long id) {
		VariantProductResponse result = variantProductService.getById(id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<VariantProductResponse>> getByProduct(@PathVariable("productId") Long productId) {
		List<VariantProductResponse> result = variantProductService.getByProduct(productId);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
//	@PreAuthorize("ADMIN")
	@PostMapping()
	public ResponseEntity<VariantProductResponse> create(@RequestBody VariantProductRequest request) {
		VariantProductResponse result = variantProductService.create(request);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}
//	@PreAuthorize("ADMIN")
	@PutMapping("/{id}")
	public ResponseEntity<VariantProductResponse> update(@PathVariable("id") Long id,
			@RequestBody VariantProductRequest request) {
		VariantProductResponse result = variantProductService.update(id, request);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
