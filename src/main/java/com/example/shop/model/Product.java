package com.example.shop.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	
	@Id 
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column (name = "code", unique = true)
	private String code;
	
	@Column (name = "created_at")
	private LocalDateTime createdAt;
	
	@Column (name = "name")
	private String name ;

	private String image;
	private int amount;
	@Column (name = "description")
	private String description;
	
	@Column (name = "price")
	private Double price;
	
	@Column( columnDefinition = "boolean default false")
	private boolean isDelete;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(mappedBy = "product")
	private Set<VariantProduct> variantProducts;

}
