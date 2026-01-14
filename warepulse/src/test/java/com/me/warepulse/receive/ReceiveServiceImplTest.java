package com.me.warepulse.receive;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.controller.dto.InventoryDto;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.inventory.service.InventoryService;
import com.me.warepulse.inventory.service.dto.IncreaseInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiveServiceImplTest {

    @InjectMocks
    ReceiveServiceImpl sut;

    @Mock
    private ReceiveRepository receiveRepository;
    @Mock
    private SkuRepository skuRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private InventoryEventService inventoryEventService;

    private Receive receive;
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

        receive = Receive.builder()
                .id(1L)
                .sku(sku)
                .location(location)
                .status(ReceiveStatus.CREATED)
                .expectedQty(10)
                .receivedQty(10)
                .inspectedBy("inspector")
                .build();
    }

    @Test
    void findReceives_success() {
        // given
        given(receiveRepository.findAll()).willReturn(List.of(receive, receive));

        // when
        List<ReceiveResponse> result = sut.findReceives();

        // then
        assertThat(result).hasSize(2);
        then(receiveRepository).should(times(1)).findAll();
        then(receiveRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findReceives_empty() {
        // given
        given(receiveRepository.findAll()).willReturn(List.of());

        // when
        List<ReceiveResponse> result = sut.findReceives();

        // then
        assertThat(result).isEmpty();
        then(receiveRepository).should().findAll();
    }

    @Test
    void findReceive_success() {
        // given
        Long receiveId = 1L;
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));

        // when
        ReceiveResponse result = sut.findReceive(receiveId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReceiveId()).isEqualTo(receive.getId());

        then(receiveRepository).should().findById(receiveId);
        then(receiveRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findReceive_fail_receive_not_found() {
        // given
        Long receiveId = 1L;
        given(receiveRepository.findById(receiveId))
                .willThrow(new WarePulseException(ErrorCode.RECEIVE_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.findReceive(receiveId))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECEIVE_NOT_FOUND);

        then(receiveRepository).should().findById(receiveId);
    }

    @Test
    void createReceive_success() {
        // given
        ReceiveRequest request = new ReceiveRequest(1L, 1L, 10);
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));

        // when
        ReceiveResponse result = sut.createReceive(request);

        // then
        assertThat(result.getExpectedQty()).isEqualTo(10);

        ArgumentCaptor<Receive> captor = ArgumentCaptor.forClass(Receive.class);
        verify(receiveRepository).save(captor.capture());
        Receive savedReceive = captor.getValue();

        assertThat(savedReceive.getExpectedQty()).isEqualTo(10);
        assertThat(savedReceive.getReceivedQty()).isEqualTo(0);
        assertThat(savedReceive.getStatus()).isEqualTo(ReceiveStatus.CREATED);
    }

    @Test
    void createReceive_fail_location_not_found() {
        // given
        ReceiveRequest request = new ReceiveRequest(101L, 1L, 10);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(101L))
                .willThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.createReceive(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOCATION_NOT_FOUND);
    }

    @Test
    void createReceive_fail_sku_not_found() {
        // given
        ReceiveRequest request = new ReceiveRequest(101L, 101L, 10);
        given(skuRepository.findById(101L))
                .willThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.createReceive(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SKU_NOT_FOUND);
    }

    @Test
    void createReceive_fail_negative_quantity() {
        // given
        ReceiveRequest request = new ReceiveRequest(1L, 1L, 0);
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));

        // when & then
        assertThatThrownBy(() -> sut.createReceive(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_RECEIVE_QUANTITY);
    }

    @Test
    void inspectedReceive_success() {
        // given
        Long receiveId = 1L;
        String username = "inspector";
        int receivedQty = 10;
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));

        // when
        ReceiveResponse result = sut.inspectedReceive(receiveId, username, receivedQty);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReceiveId()).isEqualTo(receive.getId());
        assertThat(receive.getReceivedQty()).isEqualTo(receivedQty);
        assertThat(receive.getInspectedBy()).isEqualTo(username);
        assertThat(receive.getStatus()).isEqualTo(ReceiveStatus.INSPECTED);

        then(receiveRepository).should().findById(receiveId);
        then(receiveRepository).shouldHaveNoMoreInteractions();
    }


    @Test
    void inspectedReceive_fail_receive_not_found() {
        // given
        Long receiveId = 1L;
        String username = "inspector";
        int receivedQty = 10;
        given(receiveRepository.findById(receiveId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.inspectedReceive(receiveId, username, receivedQty))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECEIVE_NOT_FOUND);
    }

    @Test
    void inspectedReceive_fail_negative_receivedQty() {
        // given
        Long receiveId = 1L;
        String username = "inspector";
        int receivedQty = 0;
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));

        // when & then
        assertThatThrownBy(() -> sut.inspectedReceive(receiveId, username, receivedQty))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_RECEIVE_QUANTITY);
    }

    @Test
    void inspectedReceive_fail_receivedQty_exceeded() {
        // given
        Long receiveId = 1L;
        String username = "inspector";
        int receivedQty = 100;
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));

        // when & then
        assertThatThrownBy(() -> sut.inspectedReceive(receiveId, username, receivedQty))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECEIVE_QTY_EXCEEDED);
    }

    @Test
    void completedReceive_success_exist_inventory() {
        // given
        Long receiveId = 1L;
        String username = "completer";
        receive.changeStatus(ReceiveStatus.INSPECTED);
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));
        given(inventoryRepository.findBySkuIdAndLocationId(receive.getSku().getId(), receive.getLocation().getId()))
                .willReturn(Optional.of(inventory));

        // when
        ReceiveResponse result = sut.completedReceive(receiveId, username);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReceiveId()).isEqualTo(receive.getId());
        assertThat(receive.getStatus()).isEqualTo(ReceiveStatus.COMPLETED);
        assertThat(receive.getCompletedBy()).isEqualTo(username);

        then(receiveRepository).should().findById(receiveId);
        then(inventoryRepository).should()
                .findBySkuIdAndLocationId(receive.getSku().getId(), receive.getLocation().getId());

        then(inventoryService).should(never()).createInventory(any());
        then(inventoryEventService).should().receive(any(IncreaseInventoryDto.class));
    }

    @Test
    void completedReceive_success_not_exist_inventory() {
        // given
        Long receiveId = 1L;
        String username = "completer";
        receive.changeStatus(ReceiveStatus.INSPECTED);
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));
        given(inventoryRepository.findBySkuIdAndLocationId(receive.getSku().getId(), receive.getLocation().getId()))
                .willReturn(Optional.empty());
        given(inventoryService.createInventory(any(InventoryDto.class))).willReturn(anyLong());

        // when
        ReceiveResponse result = sut.completedReceive(receiveId, username);

        // then
        assertThat(result).isNotNull();
        assertThat(receive.getStatus()).isEqualTo(ReceiveStatus.COMPLETED);

        then(inventoryService).should().createInventory(any(InventoryDto.class));
        then(inventoryEventService).should().receive(any(IncreaseInventoryDto.class));
    }

    @Test
    void completedReceive_fail_receive_not_found() {
        // given
        Long receiveId = 1L;
        String username = "completer";
        given(receiveRepository.findById(receiveId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.completedReceive(receiveId, username))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECEIVE_NOT_FOUND);
    }

    @Test
    void completedReceive_fail_receive_inspection_not_completed() {
        // given
        Long receiveId = 1L;
        String username = "completer";
        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));

        // when & then
        assertThatThrownBy(() -> sut.completedReceive(receiveId, username))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECEIVE_INSPECTION_NOT_COMPLETED);
    }

    @Test
    void completedReceive_fail_receive_inventory() {
        // given
        Long receiveId = 1L;
        String username = "completer";
        receive.changeStatus(ReceiveStatus.INSPECTED);

        given(receiveRepository.findById(receiveId)).willReturn(Optional.of(receive));
        given(inventoryRepository.findBySkuIdAndLocationId(receive.getSku().getId(), receive.getLocation().getId()))
                .willReturn(Optional.empty());
        given(inventoryService.createInventory(any(InventoryDto.class)))
                .willThrow(new WarePulseException(ErrorCode.DUPLICATE_INVENTORY));

        // when & then
        assertThatThrownBy(() -> sut.completedReceive(receiveId, username))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_INVENTORY);
    }
}