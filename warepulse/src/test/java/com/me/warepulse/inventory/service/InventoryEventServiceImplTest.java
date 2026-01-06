package com.me.warepulse.inventory.service;

import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.entity.InventoryEvent;
import com.me.warepulse.inventory.entity.InventoryEventType;
import com.me.warepulse.inventory.repository.InventoryEventRepository;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryEventServiceImplTest {

    @InjectMocks
    InventoryEventServiceImpl sut;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventRepository inventoryEventRepository;

    private Inventory inventory;
    private Sku sku;
    private Location location;

    @BeforeEach
    void setup() {
        sku = Sku.builder()
                .id(1L)
                .build();

        location = Location.builder()
                .id(10L)
                .build();

        inventory = Inventory.builder()
                .id(100L)
                .sku(sku)
                .location(location)
                .quantity(0)
                .build();
    }

    @Test
    void receive_success() {
        // given
        IncreaseInventoryDto request = new IncreaseInventoryDto(
                100L,
                200L,
                IncreaseReason.PURCHASE_INBOUND,
                5
        );
        when(inventoryRepository.findById(100L)).thenReturn(Optional.of(inventory));

        // when
        sut.receive(request);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(5);

        ArgumentCaptor<InventoryEvent> captor = ArgumentCaptor.forClass(InventoryEvent.class);
        verify(inventoryEventRepository).save(captor.capture());

        InventoryEvent savedEvent = captor.getValue();

        assertThat(savedEvent.getType()).isEqualTo(InventoryEventType.INCREASE);
        assertThat(savedEvent.getQuantity()).isEqualTo(5);
        assertThat(savedEvent.getSkuId()).isEqualTo(1L);
        assertThat(savedEvent.getLocationId()).isEqualTo(10L);

        assertThat(savedEvent.getPayload())
                .containsEntry("sku_id", 1L)
                .containsEntry("location_id", 10L)
                .containsEntry("quantity", 5);
    }
}