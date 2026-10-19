package com.pie.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pie.demo.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
