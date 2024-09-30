package com.example.shop.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column (name = "name")
	private String name;
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonIgnore
	@ManyToMany(mappedBy = "roles")
	private Set<User> users;
	
	
	

}
