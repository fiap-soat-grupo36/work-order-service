package br.com.fiap.oficina.workorder.repository;

import br.com.fiap.oficina.shared.enums.StatusOrdemServico;
import br.com.fiap.oficina.workorder.entity.OrdemServico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends MongoRepository<OrdemServico, String> {

    List<OrdemServico> findByStatusIn(List<StatusOrdemServico> status);

    List<OrdemServico> findByClienteId(Long clienteId);

    List<OrdemServico> findByVeiculoId(Long veiculoId);

    List<OrdemServico> findByMecanicoId(Long mecanicoId);

    @Query("{ '$or': [ { 'status': 'AGUARDANDO_APROVACAO' }, { 'orcamento_id': { '$ne': null } } ] }")
    List<OrdemServico> findOrdensAtualizadas();
}
