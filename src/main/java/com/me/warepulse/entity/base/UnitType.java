package com.me.warepulse.entity.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UnitType {
    EA("단품"), BOX("박스"), SET("세트"), PR("한쌍");

    private final String name;
}
