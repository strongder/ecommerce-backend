package com.example.shop.service;

import com.example.shop.convert.ProductConvert;
import com.example.shop.dtos.request.ProductRequest;
import com.example.shop.dtos.response.ProductResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Category;
import com.example.shop.model.ImageProduct;
import com.example.shop.model.Product;
import com.example.shop.model.VarProduct;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.VarProductRepository;
import com.example.shop.utils.PaginationSortingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

    ProductRepository productRepository;
    VarProductRepository varProductRepository;
    CategoryRepository categoryRepository;
    CartRepository cartRepository;
    ProductConvert productConvert;
    ReviewService reviewService;
    ObjectMapper objectMapper;


    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            product.get().setVarProducts(getActiveVarProducts(id));
            ProductResponse response = productConvert.convertToDTO(product.get());
            return response;//productConvert.convertToDTO(product.get());
        }
        throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
    }

    public String delete(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            product.get().getVarProducts().forEach(varProduct -> varProduct.setDelete(true));
            product.get().setDelete(true);
            return "Delete product success";
        } else {
            throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, int pageNum, int pageSize, String sortDir, String sortBy) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
            Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
            // Chuyển đổi các đối tượng Product thành ProductResponse
            return productPage.map(product -> productConvert.convertToDTO(product));
        }
        throw new AppException(ErrorResponse.CATEGORY_NOT_EXISTED);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(int pageNum, int pageSize, String sortDir, String sortBy) {
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(product -> productConvert.convertToDTO(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByKey(String key) {

        List<Product> products = productRepository.findAll().stream().filter(
                product -> product.getName().contains(key)).collect(Collectors.toList());
        return products.stream().
                map(product -> productConvert.convertToDTO(product))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productConvert.convertToEntity(request);
        product.setCreatedAt(LocalDateTime.now());
        List<ImageProduct> images = request.getImageUrls().stream()
                .map(imageUrl -> {
                    ImageProduct image = new ImageProduct();
                    image.setImageUrl(imageUrl);
                    image.setProduct(product);
                    return image;
                })
                .collect(Collectors.toList());
        product.setImageUrls(images);
        AtomicInteger stock = new AtomicInteger();
        product.setRating(0.0f);
        product.getVarProducts().stream().map(
                varProduct -> {
                    stock.addAndGet(varProduct.getStock());
                    varProduct.setProduct(product);
                    return varProduct;
                }
        ).collect(Collectors.toList());
        product.setStock(stock.get());
        productRepository.save(product);
        ProductResponse response = productConvert.convertToDTO(product);
        return response;
    }

    @Transactional
    public ProductResponse update(Long productId, ProductRequest request) {
        Optional<Product> existedProductOpt = productRepository.findById(productId);
        log.warn("check reaquest: " + request.getImageUrls().stream().findFirst().get());
        if (existedProductOpt.isPresent()) {
            Product existedProduct = existedProductOpt.get();
            existedProduct.setName(request.getName());
            existedProduct.setDescription(request.getDescription());
            existedProduct.setPrice(request.getPrice());
            existedProduct.setUpdatedAt(LocalDateTime.now());
            // Xóa các hình ảnh cũ
            existedProduct.getImageUrls().clear();
            // Thêm hình ảnh mới
            List<ImageProduct> images = request.getImageUrls().stream()
                    .map(imageUrl -> {
                        ImageProduct image = new ImageProduct();
                        image.setImageUrl(imageUrl);
                        image.setProduct(existedProduct);
                        return image;
                    })
                    .collect(Collectors.toList());
            existedProduct.getImageUrls().addAll(images);
             //Cập nhật varProducts
            existedProduct.getVarProducts().clear();
            AtomicInteger stock = new AtomicInteger();
            List<VarProduct> varProducts = request.getVarProducts().stream()
                    .map(varProductRequest -> {
                                VarProduct varProduct;
                                if (varProductRequest.getId() == null) {
                                    varProduct = new VarProduct();
                                } else {
                                    varProduct = varProductRepository.findById(varProductRequest.getId()).orElse(null);
                                }
                                try {
                                    varProduct.setAttribute(objectMapper.writeValueAsString(varProductRequest.getAttribute()));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                                varProduct.setStock(varProductRequest.getStock());
                                varProduct.setProduct(existedProduct);
                                stock.addAndGet(varProduct.getStock());
                                return varProduct;
                            }
                    ).collect(Collectors.toList());
            existedProduct.getVarProducts().addAll(varProducts);
            existedProduct.setStock(stock.get());

            productRepository.save(existedProduct);

            return productConvert.convertToDTO(existedProduct);
        } else {
            throw new AppException(ErrorResponse.PRODUCT_NOT_EXISTED);
        }
    }
//    public Page<ProductResponse> searchProduct (String query)



    public boolean isProductInStock(Long varProductId, int quantity) {
        Optional<VarProduct> varProduct = varProductRepository.findById(varProductId);
        if (varProduct.isPresent()) {
            return varProduct.get().getStock() >= quantity;
        }
        return false;
    }

    public List<VarProduct> getActiveVarProducts(Long productId) {
        List<VarProduct> varProducts = varProductRepository.findByProductId(productId);
        return varProducts.stream().filter(varProduct -> !varProduct.isDelete()).collect(Collectors.toList());
    }
}
