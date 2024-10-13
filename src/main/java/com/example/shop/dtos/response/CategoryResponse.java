package com.example.shop.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String image;
    private LocalDateTime createAt;
    private Long parentId;
    private Set<CategoryResponse> subCategories;
}
