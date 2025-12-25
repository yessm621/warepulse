package com.me.warepulse.user;

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

    @Column(length = 200, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static User createUser(String username, String password) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.role = UserRole.OPERATOR;
        return user;
    }

    public static User authUser(String username, String password, UserRole role) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.role = role;
        return user;
    }
}
