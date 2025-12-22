package com.me.warepulse.entity;

import com.me.warepulse.entity.base.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, length = 20, nullable = false, updatable = false)
    private String username;

    @Column(length = 20, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
