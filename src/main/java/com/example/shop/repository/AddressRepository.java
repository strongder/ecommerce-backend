package com.example.shop.repository;


import com.example.shop.model.Address;
import com.example.shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Set<Address> findByUser(User user);

    Set<Address> findByUserAndIsDeleteFalse(User user);

    Optional<Address> findByUserAndDefaultAddressTrueAndIsDeleteFalse(User user);
}

