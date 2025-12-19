package com.me.warepulse.entity;

import jakarta.persistence.*;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, length = 20, nullable = false)
    private String username;

    @Column(length = 20, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
