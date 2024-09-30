package com.example.shop.repository;

import com.example.shop.model.Notification;
import com.example.shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

     List<Notification> findByUser(User user);
}
