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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Page<ProductResponse> getProductByKey(String key, int pageNum, int pageSize, String sortDir, String sortBy, int rating, double price, boolean discount) {

        // Chia khóa thành các từ khóa
        String[] keyArray = key.split(" ");
        StringBuilder formatKey = new StringBuilder();

        // Ghi lại các từ khóa vào biến formatKey
        Arrays.stream(keyArray).forEach(subKey -> {
            formatKey.append("+").append(subKey);
        });

        // Logging từ khóa đã định dạng
        log.warn("Formatted search key: {}", formatKey.toString());

        // Lấy danh sách sản phẩm từ cơ sở dữ liệu
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Product> products = productRepository.searchByKeyword(formatKey.toString(), pageable);

        // Khởi tạo stream từ danh sách sản phẩm
        Stream<Product> productStream = products.stream();

        // Lọc theo điều kiện price
        if (price > 0) {
            productStream = productStream.filter(product -> product.getPrice() <= price);
        }
        // Lọc theo điều kiện rating
        if (rating > 0) {
            productStream = productStream.filter(product -> product.getRating() == rating);
        }
        // Lọc theo điều kiện discount
        if (discount) {
            productStream = productStream.filter(product -> product.getDiscount() > 0);
        }

        // Chuyển đổi các sản phẩm đã lọc thành ProductResponse và thu thập chúng vào danh sách
        List<ProductResponse> filteredProducts = productStream
                .map(product -> productConvert.convertToDTO(product))
                .collect(Collectors.toList());

        // Trả về danh sách sản phẩm đã lọc trong một Page
        return new PageImpl<>(filteredProducts, pageable, products.getTotalElements());
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
        product.setQuantitySold(0);
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

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductDiscount(int pageNum, int pageSize, String sortDir, String sortBy) {

        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Product> productPage = productRepository.findByDiscount(pageable);
        return productPage.map(product -> productConvert.convertToDTO(product));

    }


    public boolean isProductInStock(Long varProductId, int quantity) {
        Optional<VarProduct> varProduct = varProductRepository.findById(varProductId);
        if (varProduct.isPresent()) {
            return varProduct.get().getStock() >= quantity;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<VarProduct> getActiveVarProducts(Long productId) {
        List<VarProduct> varProducts = varProductRepository.findByProductId(productId);
        return varProducts.stream().filter(varProduct -> !varProduct.isDelete()).collect(Collectors.toList());
    }
}
