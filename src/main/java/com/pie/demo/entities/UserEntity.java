package com.pie.demo.entities;

import com.pie.demo.enums.Role;
import com.pie.demo.enums.UserState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "surname")
    String surname;

    @Column(name = "email", unique = true)
    String email;

    @Column(name = "password_hash")
    String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    Role role;

    @Column(name = "state")
    UserState state;

    @Column(name = "created_at")
    String createdAt;

    @Column(name = "updated_at")
    String updatedAt;

}
