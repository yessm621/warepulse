package com.me.warepulse.location;

import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.location.dto.LocationRequest;
import com.me.warepulse.location.dto.LocationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(@Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationResponse>>> findLocations() {
        List<LocationResponse> locations = locationService.findLocations();
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<ApiResponse<LocationResponse>> findLocationById(
            @PathVariable("locationId") Long locationId) {
        LocationResponse location = locationService.findLocationById(locationId);
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{locationId}")
    public ResponseEntity<ApiResponse> deleteLocation(@PathVariable("locationId") Long locationId) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }
}
