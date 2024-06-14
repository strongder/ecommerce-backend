package com.example.shop.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_line_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartLineItem {
	
	@Id 
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column (name = "price")
	private Double price;
	
	@Column (name = "amount")
	private int amount;
	
	@Column(name = "is_delete", columnDefinition = "boolean default false")
	private boolean isDelete;
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "cart_id")
	@JsonIgnore
	private Cart cart;
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "product_variant_id")
	private VariantProduct variantProduct;

}
