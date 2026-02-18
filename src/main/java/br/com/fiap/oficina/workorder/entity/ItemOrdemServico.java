package br.com.fiap.oficina.workorder.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemOrdemServico {

    @Field("produto_catalogo_id")
    private Long produtoCatalogoId;

    private Integer quantidade;

    @Field("preco_unitario")
    private BigDecimal precoUnitario;
}
