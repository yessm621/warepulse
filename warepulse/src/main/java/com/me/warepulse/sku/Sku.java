package com.me.warepulse.sku;

import com.me.warepulse.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sku")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Sku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String code;

    @Column(length = 500, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private SkuType type;

    public static Sku create(String code, String name, SkuType type) {
        Sku sku = new Sku();
        sku.code = code;
        sku.name = name;
        sku.type = type;
        return sku;
    }
}
