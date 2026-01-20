package com.me.warepulse.adjustment;

import com.me.warepulse.adjustment.dto.AdjustmentRequest;
import com.me.warepulse.adjustment.dto.AdjustmentResponse;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.entity.Inventory;
import com.me.warepulse.inventory.repository.InventoryRepository;
import com.me.warepulse.inventory.service.InventoryEventService;
import com.me.warepulse.location.Location;
import com.me.warepulse.location.LocationRepository;
import com.me.warepulse.sku.Sku;
import com.me.warepulse.sku.SkuRepository;
import com.me.warepulse.user.User;
import com.me.warepulse.user.UserRepository;
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
class AdjustmentServiceImplTest {

    @InjectMocks
    AdjustmentServiceImpl sut;

    @Mock
    private AdjustmentRepository adjustmentRepository;
    @Mock
    private SkuRepository skuRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryEventService inventoryEventService;

    private Adjustment adjustment;
    private User user;
    private Sku sku;
    private Location location;
    private Inventory inventory;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("test123")
                .build();

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

        adjustment = Adjustment.builder()
                .id(1L)
                .sku(sku)
                .location(location)
                .operator(user)
                .reason(AdjustmentReason.MANUAL_CORRECTION)
                .delta(-1)
                .build();
    }

    @Test
    void findAdjustments_success() {
        // given
        given(adjustmentRepository.findAll()).willReturn(List.of(adjustment, adjustment));

        // when
        List<AdjustmentResponse> result = sut.findAdjustments();

        // then
        assertThat(result).hasSize(2);
        then(adjustmentRepository).should().findAll();
        then(adjustmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findAdjustment_success() {
        // given
        Long adjustmentId = 1L;
        given(adjustmentRepository.findById(adjustmentId)).willReturn(Optional.of(adjustment));

        // when
        AdjustmentResponse result = sut.findAdjustment(adjustmentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAdjustmentId()).isEqualTo(adjustment.getId());

        then(adjustmentRepository).should().findById(adjustmentId);
        then(adjustmentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findAdjustment_fail() {
        // given
        Long adjustmentId = 101L;
        given(adjustmentRepository.findById(adjustmentId))
                .willThrow(new WarePulseException(ErrorCode.ADJUSTMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.findAdjustment(adjustmentId))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADJUSTMENT_NOT_FOUND);
    }

    @Test
    void createAdjustment_success() {
        // given
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(inventoryRepository.findBySkuIdAndLocationId(1L, 1L)).willReturn(Optional.of(inventory));

        // when
        AdjustmentResponse result = sut.create(request);

        // then
        ArgumentCaptor<Adjustment> captor = ArgumentCaptor.forClass(Adjustment.class);
        verify(adjustmentRepository).save(captor.capture());
        Adjustment savedAdjustment = captor.getValue();

        assertThat(savedAdjustment.getDelta()).isEqualTo(-1);
        assertThat(savedAdjustment.getReason()).isEqualTo(AdjustmentReason.MANUAL_CORRECTION);

        then(inventoryEventService).should().adjustment(any(AdjustmentInventoryDto.class));
    }

    @Test
    void createAdjustment_fail_sku_not_found() {
        // given
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        given(skuRepository.findById(1L))
                .willThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SKU_NOT_FOUND);
    }

    @Test
    void createAdjustment_fail_location_not_found() {
        // given
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(1L))
                .willThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOCATION_NOT_FOUND);
    }

    @Test
    void createAdjustment_fail_user_not_found() {
        // given
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(userRepository.findById(1L)).willThrow(new WarePulseException(ErrorCode.USER_NOT_FOUND));


        // when & then
        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void createAdjustment_fail_inventory_not_found() {
        // given
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        given(skuRepository.findById(1L)).willReturn(Optional.of(sku));
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(inventoryRepository.findBySkuIdAndLocationId(1L, 1L))
                .willThrow(new WarePulseException(ErrorCode.INVENTORY_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(WarePulseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVENTORY_NOT_FOUND);
    }
}