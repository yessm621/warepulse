package com.me.warepulse.inventory.service;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.entity.EventEnum.DecreaseReason;
import com.me.warepulse.inventory.entity.EventEnum.IncreaseReason;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.entity.InventoryEvent;
import com.me.warepulse.inventory.entity.InventoryEventType;
import com.me.warepulse.inventory.repository.InventoryEventRepository;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.dto.DecreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReleaseInventoryDto;
import com.me.warepulse.inventory.service.dto.ReserveInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.sku.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

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
                .quantity(100)
                .reservedQty(10)
                .build();
    }

    @Test
    void receive_success() {
        // given
        IncreaseInventoryDto dto = new IncreaseInventoryDto(
                100L,
                200L,
                IncreaseReason.PURCHASE_INBOUND,
                5
        );
        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when
        sut.receive(dto);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(105);

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

    @Test
    void receive_fail_inventory_not_found() {
        // given
        IncreaseInventoryDto dto = new IncreaseInventoryDto(
                101L,
                200L,
                IncreaseReason.PURCHASE_INBOUND,
                5
        );

        // when & then
        given(inventoryRepository.findById(any()))
                .willThrow(new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));

        assertThatThrownBy(() -> sut.receive(dto))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVENTORY_NOT_FOUND);

        verify(inventoryEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("입고할 재고 개수가 1 보다 작은 경우 오류 발생")
    void receive_fail_invalid_quantity() {
        IncreaseInventoryDto dto = new IncreaseInventoryDto(100L, 200L, IncreaseReason.PURCHASE_INBOUND, 0);
        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.receive(dto))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_QUANTITY);

        verify(inventoryEventRepository, never()).save(any());
    }

    @Test
    void shipment_success() {
        // given
        DecreaseInventoryDto dto = new DecreaseInventoryDto(
                100L, 200L, DecreaseReason.SHIP_OUT, 10
        );
        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when
        sut.shipment(dto);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(90);

        ArgumentCaptor<InventoryEvent> captor = ArgumentCaptor.forClass(InventoryEvent.class);
        verify(inventoryEventRepository).save(captor.capture());

        InventoryEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getType()).isEqualTo(InventoryEventType.DECREASE);
        assertThat(savedEvent.getQuantity()).isEqualTo(10);
        assertThat(savedEvent.getSkuId()).isEqualTo(1L);
        assertThat(savedEvent.getLocationId()).isEqualTo(10L);

        assertThat(savedEvent.getPayload())
                .containsEntry("sku_id", 1L)
                .containsEntry("location_id", 10L)
                .containsEntry("quantity", 10);
    }

    @Test
    void reserve_success() {
        // given
        ReserveInventoryDto dto = new ReserveInventoryDto(
                100L, 200L, DecreaseReason.RESERVED, 10
        );
        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when
        sut.reserve(dto);

        // then
        ArgumentCaptor<InventoryEvent> captor = ArgumentCaptor.forClass(InventoryEvent.class);
        verify(inventoryEventRepository).save(captor.capture());

        InventoryEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getType()).isEqualTo(InventoryEventType.RESERVE);
        assertThat(savedEvent.getQuantity()).isEqualTo(10);
        assertThat(savedEvent.getSkuId()).isEqualTo(1L);
        assertThat(savedEvent.getLocationId()).isEqualTo(10L);

        assertThat(savedEvent.getPayload())
                .containsEntry("sku_id", 1L)
                .containsEntry("location_id", 10L)
                .containsEntry("quantity", 10);
    }

    @Test
    @DisplayName("가용 재고보다 많이 예약하여 오류 발생")
    void reserve_insufficient_available_quantity() {
        // given
        inventory = Inventory.builder()
                .id(100L)
                .sku(sku)
                .location(location)
                .quantity(10)
                .reservedQty(8)
                .build();

        ReserveInventoryDto dto = new ReserveInventoryDto(100L, 200L, DecreaseReason.SHIP_OUT, 5);

        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.reserve(dto))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_INVENTORY_QUANTITY);

        verify(inventoryEventRepository, never()).save(any());
    }

    @Test
    void release_success() {
        // given
        ReleaseInventoryDto dto = new ReleaseInventoryDto(100L, 200L, IncreaseReason.RESERVED_CANCEL, 10);
        given(inventoryRepository.findById(any())).willReturn(Optional.of(inventory));

        // when
        sut.release(dto);

        // then
        ArgumentCaptor<InventoryEvent> captor = ArgumentCaptor.forClass(InventoryEvent.class);
        verify(inventoryEventRepository).save(captor.capture());

        InventoryEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getType()).isEqualTo(InventoryEventType.RELEASE);
        assertThat(savedEvent.getQuantity()).isEqualTo(10);
        assertThat(savedEvent.getSkuId()).isEqualTo(1L);
        assertThat(savedEvent.getLocationId()).isEqualTo(10L);

        assertThat(savedEvent.getPayload())
                .containsEntry("sku_id", 1L)
                .containsEntry("location_id", 10L)
                .containsEntry("quantity", 10);
    }

    @Test
    @DisplayName("예약이 없는데 예약 해제하여 오류 발생")
    void release_without_reservation() {
        // given
        inventory = Inventory.builder()
                .id(100L)
                .sku(sku)
                .location(location)
                .quantity(10)
                .reservedQty(0)
                .build();

        ReleaseInventoryDto request = new ReleaseInventoryDto(
                100L,
                200L,
                IncreaseReason.RESERVED_CANCEL,
                3
        );

        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.release(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NO_RESERVED_QUANTITY);

        verify(inventoryEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 해제할 건 수보다 예약된 재고가 적을 때 오류 발생")
    void release_insufficient_available_reserve_quantity() {
        // given
        inventory = Inventory.builder()
                .id(100L)
                .sku(sku)
                .location(location)
                .quantity(10)
                .reservedQty(1)
                .build();

        ReleaseInventoryDto request = new ReleaseInventoryDto(
                100L,
                200L,
                IncreaseReason.RESERVED_CANCEL,
                3
        );

        given(inventoryRepository.findById(100L)).willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.release(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_RELEASE_QUANTITY);

        verify(inventoryEventRepository, never()).save(any());
    }
}