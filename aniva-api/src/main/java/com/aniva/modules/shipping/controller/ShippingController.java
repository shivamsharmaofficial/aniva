package com.aniva.modules.shipping.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.core.security.CustomUserDetails;
import com.aniva.modules.shipping.dto.AssignShipmentRequest;
import com.aniva.modules.shipping.dto.CreateShippingAddressRequest;
import com.aniva.modules.shipping.dto.ShipmentResponse;
import com.aniva.modules.shipping.dto.ShippingAddressResponse;
import com.aniva.modules.shipping.service.ShippingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/shipping/addresses")
    public ApiResponse<ShippingAddressResponse> addShippingAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateShippingAddressRequest request) {

        return ApiResponse.success(
                "Shipping address added successfully",
                shippingService.addAddress(userDetails.getUserId(), request)
        );
    }

    @GetMapping("/shipping/addresses")
    public ApiResponse<List<ShippingAddressResponse>> getUserAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(
                "Shipping addresses fetched successfully",
                shippingService.getUserAddresses(userDetails.getUserId())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/shipping/shipments")
    public ApiResponse<ShipmentResponse> assignShipment(
            @RequestBody AssignShipmentRequest request) {

        return ApiResponse.success(
                "Shipment assigned successfully",
                shippingService.assignShipment(request)
        );
    }

    @GetMapping("/shipping/track/{trackingNumber}")
    public ApiResponse<ShipmentResponse> trackShipment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String trackingNumber) {

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return ApiResponse.success(
                "Shipment fetched successfully",
                shippingService.trackShipment(userDetails.getUserId(), isAdmin, trackingNumber)
        );
    }
}
