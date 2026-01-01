package com.me.warepulse.sku.dto;

import com.me.warepulse.sku.SkuType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SkuRequest {

    @NotBlank
    @Pattern(
            regexp = "^[A-Z]{2,10}-[A-Z]{2,5}-[A-Z0-9]{1,4}$",
            message = "Sku code 형식은 '[PRODUCT]-[COLOR]-[SIZE]' 이어야 합니다."
    )
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private SkuType type;
}
