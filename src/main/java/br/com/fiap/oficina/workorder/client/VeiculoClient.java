package br.com.fiap.oficina.workorder.client;

import br.com.fiap.oficina.workorder.dto.response.VeiculoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "${customer-service.url:http://localhost:8081}", contextId = "veiculo-client")
public interface VeiculoClient {

    @GetMapping("/api/veiculos/{id}")
    VeiculoResponseDTO getVeiculo(@PathVariable("id") Long id);
}