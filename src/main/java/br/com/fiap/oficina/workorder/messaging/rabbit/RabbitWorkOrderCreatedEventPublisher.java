package br.com.fiap.oficina.workorder.messaging.rabbit;

import br.com.fiap.oficina.workorder.messaging.WorkOrderCreatedEventPublisher;
import br.com.fiap.oficina.workorder.messaging.event.WorkOrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitWorkOrderCreatedEventPublisher implements WorkOrderCreatedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.messaging.work-order-created.exchange}")
    private String exchange;

    @Value("${app.messaging.work-order-created.routing-key}")
    private String routingKey;

    @Override
    public void publish(WorkOrderCreatedEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("workOrderId", event.workOrderId());
        payload.put("status", event.status());
        payload.put("createdAt", event.createdAt());
        payload.put("notes", event.notes());
        payload.put("client", event.client());
        payload.put("vehicle", event.vehicle());
        payload.put("services", event.services());
        payload.put("products", event.products());

        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        log.info("Evento work-order.created publicado para ordem={}", event.workOrderId());
    }
}
