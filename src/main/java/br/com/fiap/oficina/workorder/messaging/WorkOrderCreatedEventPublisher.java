package br.com.fiap.oficina.workorder.messaging;

import br.com.fiap.oficina.workorder.messaging.event.WorkOrderCreatedEvent;

public interface WorkOrderCreatedEventPublisher {

    void publish(WorkOrderCreatedEvent event);
}
