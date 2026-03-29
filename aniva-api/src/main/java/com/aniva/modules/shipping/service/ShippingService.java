package com.aniva.modules.shipping.service;

import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.shipping.dto.AssignShipmentRequest;
import com.aniva.modules.shipping.dto.CreateShippingAddressRequest;
import com.aniva.modules.shipping.dto.ShipmentResponse;
import com.aniva.modules.shipping.dto.ShippingAddressResponse;
import com.aniva.modules.shipping.entity.Shipment;
import com.aniva.modules.shipping.entity.ShippingAddress;
import com.aniva.modules.shipping.enums.ShipmentStatus;
import com.aniva.modules.shipping.repository.ShipmentRepository;
import com.aniva.modules.shipping.repository.ShippingAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ShippingService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserOrderRepository userOrderRepository;

    public ShippingService(
            ShippingAddressRepository shippingAddressRepository,
            ShipmentRepository shipmentRepository,
            UserOrderRepository userOrderRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
        this.shipmentRepository = shipmentRepository;
        this.userOrderRepository = userOrderRepository;
    }

    @Transactional
    public ShippingAddressResponse addAddress(Long userId, CreateShippingAddressRequest request) {

        validateAddressRequest(request);

        ShippingAddress address = ShippingAddress.builder()
                .userId(userId)
                .name(request.getName().trim())
                .phone(request.getPhone().trim())
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .pincode(request.getPincode().trim())
                .build();

        return toAddressResponse(shippingAddressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<ShippingAddressResponse> getUserAddresses(Long userId) {

        return shippingAddressRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toAddressResponse)
                .toList();
    }

    @Transactional
    public ShipmentResponse assignShipment(AssignShipmentRequest request) {

        validateShipmentRequest(request);

        var order = userOrderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Shipment shipment = shipmentRepository.findByOrderId(request.getOrderId())
                .orElseGet(Shipment::new);

        shipment.setOrderId(request.getOrderId());
        shipment.setUserId(order.getUser().getId());
        shipment.setTrackingNumber(request.getTrackingNumber().trim());
        shipment.setCourier(request.getCourier().trim());
        shipment.setStatus(resolveShipmentStatus(request.getStatus()));

        return toShipmentResponse(shipmentRepository.save(shipment));
    }

    @Transactional(readOnly = true)
    public ShipmentResponse trackShipment(Long userId, boolean isAdmin, String trackingNumber) {

        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .filter(shipment -> isAdmin || shipment.getUserId().equals(userId))
                .map(this::toShipmentResponse)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    private void validateAddressRequest(CreateShippingAddressRequest request) {

        if (!StringUtils.hasText(request.getName())) {
            throw new RuntimeException("Name is required");
        }
        if (!StringUtils.hasText(request.getPhone())) {
            throw new RuntimeException("Phone is required");
        }
        if (!StringUtils.hasText(request.getAddress())) {
            throw new RuntimeException("Address is required");
        }
        if (!StringUtils.hasText(request.getCity())) {
            throw new RuntimeException("City is required");
        }
        if (!StringUtils.hasText(request.getPincode())) {
            throw new RuntimeException("Pincode is required");
        }
    }

    private void validateShipmentRequest(AssignShipmentRequest request) {

        if (request.getOrderId() == null) {
            throw new RuntimeException("Order ID is required");
        }
        if (!StringUtils.hasText(request.getTrackingNumber())) {
            throw new RuntimeException("Tracking number is required");
        }
        if (!StringUtils.hasText(request.getCourier())) {
            throw new RuntimeException("Courier is required");
        }
    }

    private ShipmentStatus resolveShipmentStatus(String status) {
        return StringUtils.hasText(status)
                ? ShipmentStatus.valueOf(status.trim().toUpperCase())
                : ShipmentStatus.ASSIGNED;
    }

    private ShippingAddressResponse toAddressResponse(ShippingAddress address) {

        return ShippingAddressResponse.builder()
                .id(address.getId())
                .userId(address.getUserId())
                .name(address.getName())
                .phone(address.getPhone())
                .address(address.getAddress())
                .city(address.getCity())
                .pincode(address.getPincode())
                .build();
    }

    private ShipmentResponse toShipmentResponse(Shipment shipment) {

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .courier(shipment.getCourier())
                .status(shipment.getStatus())
                .build();
    }
}
