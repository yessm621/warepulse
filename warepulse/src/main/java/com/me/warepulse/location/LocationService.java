package com.me.warepulse.location;

import com.me.warepulse.location.dto.LocationRequest;
import com.me.warepulse.location.dto.LocationResponse;

import java.util.List;

public interface LocationService {

    LocationResponse createLocation(LocationRequest request);

    List<LocationResponse> findLocations();

    List<LocationResponse> findLocationByWarehouseId(Long warehouseId);

    LocationResponse findLocationById(Long locationId);

    void deleteLocation(Long locationId);
}
