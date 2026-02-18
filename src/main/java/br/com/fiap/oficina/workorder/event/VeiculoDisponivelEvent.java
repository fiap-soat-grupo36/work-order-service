package br.com.fiap.oficina.workorder.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class VeiculoDisponivelEvent extends ApplicationEvent {

    private final String ordemServicoId;
    private final Long veiculoId;

    public VeiculoDisponivelEvent(Object source, String ordemServicoId, Long veiculoId) {
        super(source);
        this.ordemServicoId = ordemServicoId;
        this.veiculoId = veiculoId;
    }
}