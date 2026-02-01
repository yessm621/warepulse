package com.me.inventoryservice.entity;

import com.me.inventoryservice.utils.BaseEntity;
import com.me.inventoryservice.utils.PayloadJsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "inventory_event")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class InventoryEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_event_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventories;
    
    private Long skuId;
    
    private Long locationId;

    @Enumerated(EnumType.STRING)
    private InventoryEventType type;

    private int quantity;

    @Convert(converter = PayloadJsonMapConverter.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> payload;

    public static InventoryEvent create(Inventory inventory, Long skuId, Long locationId, InventoryEventType type, int quantity, Map<String, Object> payload) {
        InventoryEvent inventoryEvent = new InventoryEvent();
        inventoryEvent.inventories = inventory;
        inventoryEvent.skuId = skuId;
        inventoryEvent.locationId = locationId;
        inventoryEvent.type = type;
        inventoryEvent.quantity = quantity;
        inventoryEvent.payload = payload;
        return inventoryEvent;
    }
}
