package com.example.shop.service;

import com.example.shop.dtos.request.CategoryRequest;
import com.example.shop.dtos.response.CategoryResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Category;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.utils.PaginationSortingUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllPaging(int pageNum, int pageSize, String sortDir, String sortBy) {
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Category> categories = categoryRepository.findByIsDeleteFalse(pageable);
        return categories.map(category -> modelMapper.map(category, CategoryResponse.class));

    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> modelMapper.map(category, CategoryResponse.class)).toList();

    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return modelMapper.map(category, CategoryResponse.class);
        } else
            throw new AppException(ErrorResponse.CATEGORY_NOT_EXISTED);
    }


    public CategoryResponse create(CategoryRequest request) {
        Optional<Category> existCategory = categoryRepository.findByName(request.getName());
        Category category = new Category();
        if (existCategory.isPresent()) {
            throw new AppException(ErrorResponse.CATEGORY_EXISTED);
        } else {
            if (request.getParentId() != null) {
                Category parentCategory = categoryRepository.findById(request.getParentId()).orElseThrow(
                        () -> new AppException(ErrorResponse.CATEGORY_NOT_EXISTED)
                );
                category.setParentCategory(parentCategory);
                parentCategory.getSubCategories().add(category);

            }
            category.setImage(request.getImage());
            category.setName(request.getName());
            category.setCreateAt(LocalDateTime.now());
            categoryRepository.save(category);
        }
        return modelMapper.map(category, CategoryResponse.class);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Category categoryUpdate = category.get();
            categoryUpdate.setName(request.getName());
            categoryUpdate.setImage(request.getImage());
            if (request.getParentId() != null) {
                Category parentCategory = categoryRepository.findById(request.getParentId()).orElseThrow(
                        () -> new AppException(ErrorResponse.CATEGORY_NOT_EXISTED)

                );
                categoryUpdate.setParentCategory(parentCategory);
                parentCategory.getSubCategories().add(categoryUpdate);
            }
            categoryRepository.save(categoryUpdate);
            return modelMapper.map(categoryUpdate, CategoryResponse.class);
        } else {
            throw new AppException(ErrorResponse.CATEGORY_NOT_EXISTED);
        }
    }

    public CategoryResponse delete(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            category.get().setDelete(true);
            categoryRepository.save(category.get());
            return modelMapper.map(category.get(), CategoryResponse.class);
        } else {
            throw new AppException(ErrorResponse.CATEGORY_NOT_EXISTED);
        }
    }

    public List<CategoryResponse> fetchParentCategory ()
    {
        List<Category> categories = categoryRepository.findByParentCategoryIsNullAndIsDeleteFalse();
        return categories.stream().map(category -> modelMapper.map(category, CategoryResponse.class)).toList();
    }


}
