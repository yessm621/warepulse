package com.me.warepulse.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN("관리자"), OPERATOR("운영자"), SYSTEM("시스템");

    private final String name;
}
