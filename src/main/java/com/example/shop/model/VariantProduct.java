package com.example.shop.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "variant_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantProduct {
	
	@Id 
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;
	@Column (name = "name")
	private String name ;
	private String code;
	private int amount;
	private String image;
	@Column (name = "color")
	private String color;
	@Column (name = "price")
	private Double price;
	@Column (name = "size")
	private String size;
	@Column( columnDefinition = "boolean default false")
	private boolean isDelete;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "variantProduct")
	@JsonIgnore
	private Set<CartLineItem> cartLineItems;
	@ManyToOne
	@JoinColumn(name = "product_id")
	@JsonIgnore
	private Product product;
	
	
	
	


}
