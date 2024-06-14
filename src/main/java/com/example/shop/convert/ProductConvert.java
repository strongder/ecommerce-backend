package com.example.shop.convert;

import com.example.shop.dtos.request.ProductRequest;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Category;
import com.example.shop.model.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductConvert {

    ModelMapper modelMapper;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    public Product convertToEntity(ProductRequest request)
    {
        Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(()-> new AppException(ErrorResponse.CATEGORY_NOT_EXISTED));
        Product product  = modelMapper.map(request, Product.class);
        product.setCategory(category);
        return  product;
    }

    public ProductResponse convertToDTO(Product product)
    {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        return response;
    }
}
