package com.me.warepulse.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {

    private Long warehouseId;

    //todo:: 컨트롤러에서 @RequestBody 앞에 @Valid를 입력해야 하는지 테스트
    @NotBlank
    @Pattern(
            regexp = "^[A-Z]-(0[1-9]|[1-9][0-9])-(0[1-9]|[1-9][0-9])$",
            message = "Location code 형식은 'A-01-02' 이어야 합니다."
    )
    private String code;

    @Max(200)
    @Min(0)
    private int capacity;
}
