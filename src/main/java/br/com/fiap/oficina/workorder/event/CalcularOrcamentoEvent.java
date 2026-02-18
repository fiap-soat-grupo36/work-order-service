package br.com.fiap.oficina.workorder.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CalcularOrcamentoEvent extends ApplicationEvent {

    private final String ordemServicoId;

    public CalcularOrcamentoEvent(Object source, String ordemServicoId) {
        super(source);
        this.ordemServicoId = ordemServicoId;
    }
}