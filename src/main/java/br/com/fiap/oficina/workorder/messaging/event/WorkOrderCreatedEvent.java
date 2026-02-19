package br.com.fiap.oficina.workorder.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record WorkOrderCreatedEvent(
        String workOrderId,
        String status,
        LocalDateTime createdAt,
        String notes,
        ClientData client,
        VehicleData vehicle,
        List<ServiceData> services,
        List<ProductData> products
) {
    public record ClientData(
            Long id,
            String name,
            String email,
            String phone
    ) {
    }

    public record VehicleData(
            Long id,
            String plate,
            String brand,
            String model,
            Integer year
    ) {
    }

    public record ServiceData(
            Long id,
            String name,
            String description,
            BigDecimal basePrice
    ) {
    }

    public record ProductData(
            Long productCatalogId,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }
}
