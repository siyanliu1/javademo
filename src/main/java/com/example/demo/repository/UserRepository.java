package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Projection method to list only id and username
    List<UserProjection> findAllBy();
}
