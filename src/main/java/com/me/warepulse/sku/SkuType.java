package com.me.warepulse.sku;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SkuType {
    EA("단품"), BOX("박스"), SET("세트"), PR("한쌍");

    private final String name;
}
