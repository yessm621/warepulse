package com.me.warepulse.inventory.entity;

import com.me.warepulse.utils.BaseEntity;
import com.me.warepulse.utils.PayloadJsonMapConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "inventory_event")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_event_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventories;

    // event 기록용 skuId, locationId
    private Long skuId;
    private Long locationId;

    @Enumerated(EnumType.STRING)
    private InventoryEventType type;

    private int quantity;

    // todo:: payload, json 타입
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
