package com.example.shop.convert;


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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VariantProductConvert {

    ModelMapper modelMapper;
    VariantProductRepository variantProductRepository;
    ProductRepository productRepository;

    public VariantProduct convertToEntity (VariantProductRequest request)
    {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()-> new AppException(ErrorResponse.PRODUCT_NOT_EXISTED));
        VariantProduct variantProduct = modelMapper.map(request, VariantProduct.class);
        variantProduct.setProduct(product);
        return variantProduct;
    }
    public VariantProductResponse convertToDTO(VariantProduct variantProduct){
        return modelMapper.map(variantProduct, VariantProductResponse.class);
    }
}
