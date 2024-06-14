package com.example.shop.controller;

import com.example.shop.dtos.request.ProductRequest;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getById(@PathVariable("id") Long id) {
		ProductResponse result = productService.getById(id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

    @GetMapping("/search/{key}")
    public ResponseEntity<List<ProductResponse>> getProductByKey(@PathVariable("key") String key) {
       List<ProductResponse> result = productService.getProductByKey(key);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

	@GetMapping()
	// path: /products/{categoryId}?pageNum= &pageSize= $sortDir= &sortBy=
	public ResponseEntity<List<ProductResponse>> getProductsByCategory(
			@RequestParam("categoryId") Long categoryId,
			@RequestParam("pageNum") int pageNum,
			@RequestParam("pageSize") int pageSize,
			@RequestParam("sortDir") String sortDir,
			@RequestParam("sortBy") String sortBy) {
		List<ProductResponse> productDTOS = productService.getProductsByCategory(categoryId, pageNum, pageSize, sortDir,
				sortBy);
		return new ResponseEntity<>(productDTOS, HttpStatus.OK);

	}
//	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping()
	public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request)
	{
		ProductResponse result = productService.create(request);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}
//	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> update(
			@PathVariable("id") Long id,
			@RequestBody ProductRequest request)
	{
        ProductResponse result = productService.update(id, request);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
