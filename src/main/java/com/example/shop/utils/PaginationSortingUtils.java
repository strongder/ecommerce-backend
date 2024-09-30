package com.example.shop.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationSortingUtils {
    public static Pageable getPageable(int pageNo, int pageSize, String sortDir, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return pageable;
    }
}
