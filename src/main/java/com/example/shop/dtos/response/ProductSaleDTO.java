package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaleDTO {

    private String image;
    private String name;
    private Integer quantitySold;
    private Double totalSales;

}
