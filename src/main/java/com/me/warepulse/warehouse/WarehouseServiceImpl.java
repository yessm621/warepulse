package com.me.warepulse.warehouse;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.warehouse.dto.WarehouseRequest;
import com.me.warepulse.warehouse.dto.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional
    @Override
    public WarehouseResponse create(WarehouseRequest request) {
        Warehouse warehouse = Warehouse.create(request.getName(), request.getAddress());
        warehouseRepository.save(warehouse);
        return WarehouseResponse.from(warehouse);
    }

    @Override
    public List<WarehouseResponse> findWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(WarehouseResponse::from)
                .toList();
    }

    @Override
    public WarehouseResponse findWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));
        return WarehouseResponse.from(warehouse);
    }

    @Transactional
    @Override
    public void deleteWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));
        warehouseRepository.delete(warehouse);
    }
}
