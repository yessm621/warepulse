package com.me.warepulse.location;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.dto.LocationRequest;
import com.me.warepulse.location.dto.LocationResponse;
import com.me.warepulse.warehouse.Warehouse;
import com.me.warepulse.warehouse.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    @Override
    public LocationResponse createLocation(LocationRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));
        Location location = Location.create(request.getCode(), request.getCapacity(), warehouse);
        locationRepository.save(location);
        return LocationResponse.from(location);
    }

    @Override
    public List<LocationResponse> findLocations() {
        return locationRepository.findAll()
                .stream()
                .map(LocationResponse::from)
                .toList();
    }

    @Override
    public List<LocationResponse> findLocationByWarehouseId(Long warehouseId) {
        return locationRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(LocationResponse::from)
                .toList();
    }

    @Override
    public LocationResponse findLocationById(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));
        return LocationResponse.from(location);
    }

    @Transactional
    @Override
    public void deleteLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));
        locationRepository.delete(location);
    }
}
