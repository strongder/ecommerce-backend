package com.example.shop.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attribute", columnDefinition = "json")
    private String attribute;

    @Column(name = "stock")
    private int stock;

    @Column(name = "is_delete", columnDefinition = "boolean default false")
    private boolean isDelete;

    @OneToMany(mappedBy = "varProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "varProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<OrderItem> orderItems;

    @ManyToOne
    private Product product;
}
