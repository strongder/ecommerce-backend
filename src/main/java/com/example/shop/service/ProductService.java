package com.example.shop.service;

import com.example.shop.convert.ProductConvert;
import com.example.shop.dtos.request.ProductRequest;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Category;
import com.example.shop.model.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import com.shop.demo.util.PaginationSortingUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService{

	 ProductRepository productRepository;
	 CategoryRepository categoryRepository;
     ProductConvert productConvert;

	@Transactional(readOnly = true)
	public ProductResponse getById(Long id) {
		Optional<Product> product = productRepository.findById(id);
		if (product.isPresent()) {
			return productConvert.convertToDTO(product.get());
		}
		throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortDir,
                                                       String sortBy) {
		Pageable pageable = PaginationSortingUtils.getPageable(pageNo, pageSize, sortDir, sortBy);
		Optional<Category> category = categoryRepository.findById(categoryId);
		if (category.isPresent()) {
			Page<Product> productPage = productRepository.findByCategory(category, pageable);
			return productPage.map(product -> productConvert.convertToDTO(product)).getContent();
		} else
			throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getAll() {
		return productRepository.findAll().stream().
                map(product -> productConvert.convertToDTO(product))
                .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getProductByKey(String key) {

		List<Product> products = productRepository.findAll().stream().filter(
				product -> product.getName().contains(key)||product.getCode().contains(key)
		).collect(Collectors.toList());
		return products.stream().
				map(product -> productConvert.convertToDTO(product))
				.collect(Collectors.toList());
	}

	@Transactional
	public ProductResponse create(ProductRequest request) {
		Product product = productConvert.convertToEntity(request);
		product.setCreatedAt(LocalDateTime.now());
		productRepository.save(product);
		return productConvert.convertToDTO(product);
	}

	@Transactional
	public ProductResponse update(Long productId, ProductRequest request) {
		Optional<Product> existedProduct = productRepository.findById(productId);
		if (existedProduct.isPresent())
		{
			Product product = existedProduct.get();
			product = productConvert.convertToEntity(request);
			product.setId(productId);
			productRepository.save(product);
			return productConvert.convertToDTO(product);
		}
		else {
			throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
		}
	}

}
