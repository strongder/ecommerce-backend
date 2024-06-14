package com.example.shop.service;

import com.example.shop.convert.VariantProductConvert;
import com.example.shop.dtos.request.VariantProductRequest;
import com.example.shop.dtos.response.VariantProductResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Product;
import com.example.shop.model.VariantProduct;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.VariantProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VariantProductService{

	private VariantProductRepository variantProductRepository;
	private ProductRepository productRepository;
	private VariantProductConvert variantProductConvert;

	@Transactional(readOnly = true)
	public VariantProductResponse getById(Long id) {
		Optional<VariantProduct> variantProduct = variantProductRepository.findById(id);
		if (variantProduct.isPresent()) {
			return variantProductConvert.convertToDTO(variantProduct.get());
		}
		throw new AppException(ErrorResponse.USER_NOT_EXISTED);
	}
	@Transactional(readOnly = true)
	public List<VariantProductResponse> getByProduct(Long productId) {
		Optional<Product> existedProduct= productRepository.findById(productId);
		if(existedProduct.isPresent())
		{
			List<VariantProduct> variantProducts = variantProductRepository.findByProductId(productId);
			return variantProducts.stream().map(variantProduct -> variantProductConvert.convertToDTO(variantProduct)).toList();
		}
		else throw new AppException(ErrorResponse.USER_NOT_EXISTED);
	}
	@Transactional
	public VariantProductResponse create(VariantProductRequest request) {
		Optional<VariantProduct> existedVariantProduct = variantProductRepository.findByCode(request.getCode());
		if (existedVariantProduct.isPresent())
		{
			throw new AppException(ErrorResponse.PRODUCT_EXISTED);
		}
		else {
			VariantProduct variantProduct = variantProductConvert.convertToEntity(request);
			Product product = variantProduct.getProduct();
			product.setAmount(product.getAmount()+variantProduct.getAmount());
			variantProductRepository.save(variantProduct);
			productRepository.save(product);
			return variantProductConvert.convertToDTO(variantProduct);
		}
	}
	@Transactional
	public VariantProductResponse update(Long id, VariantProductRequest request) {
		Optional<VariantProduct> existedVariantProduct = variantProductRepository.findById(id);
		if (existedVariantProduct.isPresent())
		{
			VariantProduct variantProduct = existedVariantProduct.get();
			Product product = variantProduct.getProduct();
			product.setAmount(product.getAmount()-variantProduct.getAmount()+ request.getAmount());
			variantProduct = variantProductConvert.convertToEntity(request);
			variantProduct.setId(id);
			variantProductRepository.save(variantProduct);
			productRepository.save(product);
			return variantProductConvert.convertToDTO(variantProduct);
		}
		else {
			throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
		}
	}

	public List<VariantProductResponse> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
