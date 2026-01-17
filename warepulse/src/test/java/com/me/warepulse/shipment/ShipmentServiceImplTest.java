package com.me.warepulse.shipment;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.inventory.service.dto.DecreaseInventoryDto;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.shipment.dto.ShipmentRequest;
import com.me.warepulse.shipment.dto.ShipmentResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @InjectMocks
    ShipmentServiceImpl sut;

    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private SkuRepository skuRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryEventService inventoryEventService;

    private Shipment shipment;
    private Inventory inventory;
    private Sku sku;
    private Location location;

    @BeforeEach
    void setup() {
        sku = Sku.builder()
                .id(1L)
                .build();

        location = Location.builder()
                .id(1L)
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .sku(sku)
                .location(location)
                .quantity(100)
                .reservedQty(10)
                .build();

        shipment = Shipment.builder()
                .id(1L)
                .sku(sku)
                .location(location)
                .status(ShipmentStatus.CREATED)
                .quantity(10)
                .pickedQty(10)
                .build();
    }

    @Test
    void findShipments_success() {
        // given
        given(shipmentRepository.findAll()).willReturn(List.of(shipment, shipment));

        // when
        List<ShipmentResponse> result = sut.findShipments();

        // then
        assertThat(result).hasSize(2);
        then(shipmentRepository).should().findAll();
        then(shipmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findShipment_success() {
        // given
        Long shipmentId = 1L;
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));

        // when
        ShipmentResponse result = sut.findShipment(shipmentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getShipmentId()).isEqualTo(shipment.getId());

        then(shipmentRepository).should().findById(shipmentId);
        then(shipmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findShipment_fail_shipment_not_found() {
        // given
        Long shipmentId = 101L;
        given(shipmentRepository.findById(shipmentId))
                .willThrow(new WarePulseException(ErrorCode.SHIPMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.findShipment(shipmentId))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_NOT_FOUND);
    }

    @Test
    void createShipment_success() {
        // given
        Long skuId = 1L;
        Long locaitonId = 1L;
        ShipmentRequest request = new ShipmentRequest(locaitonId, skuId, 10);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(inventoryRepository.findBySkuIdAndLocationId(skuId, locaitonId)).willReturn(Optional.of(inventory));

        // when
        ShipmentResponse result = sut.createShipment(request);

        // then
        assertThat(result.getQuantity()).isEqualTo(10);

        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        Shipment savedShipment = captor.getValue();

        assertThat(savedShipment.getQuantity()).isEqualTo(10);
        assertThat(savedShipment.getPickedQty()).isEqualTo(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.CREATED);
    }

    @Test
    void createShipment_fail_sku_not_found() {
        // given
        ShipmentRequest request = new ShipmentRequest(1L, 101L, 10);
        given(skuRepository.findById(101L))
                .willThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.createShipment(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SKU_NOT_FOUND);
    }

    @Test
    void createShipment_fail_location_not_found() {
        // given
        ShipmentRequest request = new ShipmentRequest(101L, 1L, 10);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(101L))
                .willThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.createShipment(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOCATION_NOT_FOUND);
    }

    @Test
    void createShipment_fail_shipment_invalid_quantity() {
        // given
        Long skuId = 1L;
        Long locaitonId = 1L;
        ShipmentRequest request = new ShipmentRequest(locaitonId, skuId, 0);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(inventoryRepository.findBySkuIdAndLocationId(skuId, locaitonId)).willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.createShipment(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_SHIPMENT_QUANTITY);
    }

    @Test
    void pickingShipment_success() {
        // given
        Long shipmentId = 1L;
        int pickedQty = 10;
        String pickingBy = "pickingBy";
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));
        given(inventoryRepository.findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId()))
                .willReturn(Optional.of(inventory));

        // when
        ShipmentResponse result = sut.pickingShipment(shipmentId, pickedQty, pickingBy);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getShipmentId()).isEqualTo(shipment.getId());
        assertThat(shipment.getPickedQty()).isEqualTo(pickedQty);
        assertThat(shipment.getPickedBy()).isEqualTo(pickingBy);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.PICKING);

        then(shipmentRepository).should().findById(shipmentId);
        then(shipmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void pickingShipment_fail_shipment_not_found() {
        // given
        Long shipmentId = 101L;
        int pickedQty = 10;
        String pickingBy = "pickingBy";
        given(shipmentRepository.findById(shipmentId))
                .willThrow(new WarePulseException(ErrorCode.SHIPMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.pickingShipment(shipmentId, pickedQty, pickingBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_NOT_FOUND);
    }

    @Test
    void pickingShipment_fail_shipment_invalid_quantity() {
        // given
        Long shipmentId = 1L;
        int pickedQty = 0;
        String pickingBy = "pickingBy";
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));
        given(inventoryRepository.findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId()))
                .willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.pickingShipment(shipmentId, pickedQty, pickingBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_SHIPMENT_QUANTITY);
    }

    @Test
    void pickingShipment_fail_shipment_picking_qty_exceeded() {
        // given
        Long shipmentId = 1L;
        int pickedQty = 100;
        String pickingBy = "pickingBy";
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));
        given(inventoryRepository.findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId()))
                .willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> sut.pickingShipment(shipmentId, pickedQty, pickingBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_QTY_EXCEEDED);
    }

    @Test
    void pickingShipment_fail_shipment_inspection_invalid_status_created() {
        // given
        Long shipmentId = 1L;
        int pickedQty = 100;
        String pickingBy = "pickingBy";
        shipment.changeStatus(ShipmentStatus.SHIPPED);
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));

        // when & then
        assertThatThrownBy(() -> sut.pickingShipment(shipmentId, pickedQty, pickingBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_INSPECTION_INVALID_STATUS_CREATED);
    }

    @Test
    void shippedShipment_success() {
        // given
        Long shipmentId = 1L;
        String shippedBy = "shippedBy";
        shipment.changeStatus(ShipmentStatus.PICKING);
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));
        given(inventoryRepository.findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId()))
                .willReturn(Optional.of(inventory));

        // when
        ShipmentResponse result = sut.shippedShipment(shipmentId, shippedBy);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getShipmentId()).isEqualTo(shipment.getId());
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.SHIPPED);
        assertThat(shipment.getShippedBy()).isEqualTo(shippedBy);

        then(shipmentRepository).should().findById(shipmentId);
        then(inventoryRepository).should()
                .findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId());

        then(inventoryEventService).should().shipment(any(DecreaseInventoryDto.class));
    }

    @Test
    void shippedShipment_fail_shipment_not_found() {
        // given
        Long shipmentId = 101L;
        String shippedBy = "shippedBy";
        given(shipmentRepository.findById(shipmentId))
                .willThrow(new WarePulseException(ErrorCode.SHIPMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.shippedShipment(shipmentId, shippedBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_NOT_FOUND);
    }

    @Test
    void shippedShipment_fail_shipment_inspection_not_completed() {
        // given
        Long shipmentId = 1L;
        String shippedBy = "shippedBy";
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));

        // when & then
        assertThatThrownBy(() -> sut.shippedShipment(shipmentId, shippedBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_INSPECTION_NOT_COMPLETED);
    }

    @Test
    void shippedShipment_fail_inventory_not_found() {
        // given
        Long shipmentId = 1L;
        String shippedBy = "shippedBy";
        shipment.changeStatus(ShipmentStatus.PICKING);
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));
        given(inventoryRepository.findBySkuIdAndLocationId(shipment.getSku().getId(), shipment.getLocation().getId()))
                .willThrow(new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.shippedShipment(shipmentId, shippedBy))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVENTORY_NOT_FOUND);
    }

    @Test
    void canceledShipment_success() {
        // given
        Long shipmentId = 1L;
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));

        //
        ShipmentResponse result = sut.canceledShipment(shipmentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getShipmentId()).isEqualTo(shipment.getId());
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.CANCELED);
        assertThat(shipment.getQuantity()).isEqualTo(0);
        assertThat(shipment.getPickedQty()).isEqualTo(0);
    }

    @Test
    void canceledShipment_fail_shipment_not_found() {
        // given
        Long shipmentId = 1L;
        given(shipmentRepository.findById(shipmentId))
                .willThrow(new WarePulseException(ErrorCode.SHIPMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.canceledShipment(shipmentId))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_NOT_FOUND);
    }

    @Test
    void canceledShipment_fail_shipment_already_shipped() {
        // given
        Long shipmentId = 1L;
        shipment.changeStatus(ShipmentStatus.SHIPPED);
        given(shipmentRepository.findById(shipmentId)).willReturn(Optional.of(shipment));

        // when & then
        assertThatThrownBy(() -> sut.canceledShipment(shipmentId))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHIPMENT_ALREADY_SHIPPED);
    }
}