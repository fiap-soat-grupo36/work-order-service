package br.com.fiap.oficina.workorder.entity;

import br.com.fiap.oficina.shared.enums.StatusOrdemServico;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "ordens_servico")
@Getter
@Setter
public class OrdemServico {

    @Id
    private String id;

    private StatusOrdemServico status = StatusOrdemServico.RECEBIDA;

    @Field("data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Field("data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;

    @Field("data_termino_execucao")
    private LocalDateTime dataTerminoExecucao;

    @Field("data_entrega")
    private LocalDateTime dataEntrega;

    private String observacoes;

    @Field("veiculo_id")
    private Long veiculoId;

    @Field("cliente_id")
    private Long clienteId;

    @Field("mecanico_id")
    private Long mecanicoId;

    @Field("orcamento_id")
    private Long orcamentoId;

    @Field("servicos_ids")
    private List<Long> servicosIds = new ArrayList<>();

    @Field("itens")
    private List<ItemOrdemServico> itensOrdemServico = new ArrayList<>();

    public void addServico(Long servicoId) {
        if (servicosIds == null) {
            servicosIds = new ArrayList<>();
        }
        if (!servicosIds.contains(servicoId)) {
            servicosIds.add(servicoId);
        }
    }

    public void removeServico(Long servicoId) {
        if (servicosIds != null) {
            servicosIds.remove(servicoId);
        }
    }

    public void addProduto(ItemOrdemServico produto) {
        if (itensOrdemServico == null) {
            itensOrdemServico = new ArrayList<>();
        }
        itensOrdemServico.add(produto);
    }

    public void removeProduto(Long produtoId) {
        if (itensOrdemServico != null) {
            itensOrdemServico.removeIf(item -> item.getProdutoCatalogoId().equals(produtoId));
        }
    }
}
