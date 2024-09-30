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
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class CategoryService{

	private CategoryRepository categoryRepository;
	private ModelMapper modelMapper;

	@Transactional(readOnly = true)
	public Page<CategoryResponse> getAll(int pageNum, int pageSize, String sortDir, String sortBy) {
        Pageable pageable = PaginationSortingUtils.getPageable(pageNum, pageSize, sortDir, sortBy);
        Page<Category> categories = categoryRepository.findAll(pageable);
		return categories.map(category -> modelMapper.map(category, CategoryResponse.class));

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
        if(existCategory.isPresent()) {
            throw new AppException(ErrorResponse.CATEGORY_EXISTED);
        }
            Category category = modelMapper.map(request, Category.class);
            category.setCreateAt(LocalDateTime.now());
            categoryRepository.save(category);
            return modelMapper.map(category, CategoryResponse.class);
	}
    public CategoryResponse update(Long id, CategoryRequest request) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Category categoryUpdate = category.get();
            categoryUpdate.setName(request.getName());
            categoryUpdate.setImage(request.getImage());
            categoryRepository.save(categoryUpdate);
            return modelMapper.map(categoryUpdate, CategoryResponse.class);
        } else {
            throw new AppException(ErrorResponse.CATEGORY_NOT_EXISTED);
        }
    }
    public void delete(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryRepository.deleteById(id);
        } else {
            throw new AppException(ErrorResponse.CATEGORY_NOT_EXISTED);
        }
    }




}
