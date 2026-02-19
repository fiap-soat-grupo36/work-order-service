# Arquitetura do Work Order Service - MongoDB (NoSQL)

## 📊 Visão Geral

O Work Order Service é responsável pelo gerenciamento de ordens de serviço da oficina mecânica, implementado com **MongoDB** como banco de dados NoSQL para demonstrar a capacidade de trabalhar com diferentes paradigmas de persistência.

## 🏗️ Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────┐
│                    API Layer (REST)                      │
│                  Controllers + DTOs                      │
├─────────────────────────────────────────────────────────┤
│                   Service Layer                          │
│              Business Logic + Events                     │
├─────────────────────────────────────────────────────────┤
│                 Integration Layer                        │
│         Feign Clients (customer, catalog)               │
├─────────────────────────────────────────────────────────┤
│                 Persistence Layer                        │
│          MongoDB Repositories + Documents                │
├─────────────────────────────────────────────────────────┤
│                  Data Store Layer                        │
│              MongoDB Atlas (Cloud)                       │
└─────────────────────────────────────────────────────────┘
```

---

## 🔧 Componentes Principais

### **1. API Layer (Controllers)**

**Responsabilidade:** Expor endpoints REST e validar requisições

```java
@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {
    // Endpoints REST
}
```

**Endpoints:**
- `POST /api/ordens-servico` - Criar OS
- `GET /api/ordens-servico` - Listar OSs
- `GET /api/ordens-servico/{id}` - Buscar por ID (String/ObjectId)
- `PUT /api/ordens-servico/{id}` - Atualizar OS
- `DELETE /api/ordens-servico/{id}` - Deletar OS
- `GET /api/ordens-servico/por-cliente/{clienteId}` - Listar por cliente
- `GET /api/ordens-servico/por-veiculo/{veiculoId}` - Listar por veículo

**Tecnologias:**
- Spring Web MVC
- Spring Validation
- Swagger/OpenAPI 3

---

### **2. Service Layer (Lógica de Negócio)**

**Responsabilidade:** Implementar regras de negócio e coordenar operações

```java
@Service
public class OrdemServicoServiceImpl implements OrdemServicoService {
    
    // Validação via Feign Client
    ClienteResponseDTO cliente = clienteClient.getCliente(clienteId);
    VeiculoResponseDTO veiculo = veiculoClient.getVeiculo(veiculoId);
    
    // Persistência no MongoDB
    OrdemServico os = repository.save(ordemServico);
    
    // Publicação de eventos
    eventPublisher.publishEvent(new CalcularOrcamentoEvent(this, os.getId()));
}
```

**Funcionalidades:**
- Validação de cliente/veículo via integração
- Cálculo de orçamento
- Gerenciamento de status da OS
- Publicação de eventos de domínio

---

### **3. Integration Layer (Feign Clients)**

**Responsabilidade:** Comunicação com outros microsserviços

#### **Cliente Service (PostgreSQL - Porta 8081)**

```java
@FeignClient(name = "customer-service", url = "${customer-service.url:}")
public interface ClienteClient {
    @GetMapping("/api/clientes/{id}")
    ClienteResponseDTO getCliente(@PathVariable Long id);
}

@FeignClient(name = "customer-service", url = "${customer-service.url:}")
public interface VeiculoClient {
    @GetMapping("/api/veiculos/{id}")
    VeiculoResponseDTO getVeiculo(@PathVariable Long id);
}
```

#### **Catalog Service (PostgreSQL - Porta 8083)**

```java
@FeignClient(name = "catalog-service", url = "${catalog-service.url:}")
public interface ServicoClient {
    @GetMapping("/api/servicos/{id}")
    ServicoResponseDTO getServico(@PathVariable Long id);
}

@FeignClient(name = "catalog-service", url = "${catalog-service.url:}")
public interface ProdutoCatalogoClient {
    @GetMapping("/api/catalogo-produtos/{id}")
    ProdutoCatalogoResponseDTO getProduto(@PathVariable Long id);
}
```

**Configuração:**
- **Local:** URLs diretas (localhost:8081, localhost:8083)
- **Kubernetes:** Service Discovery via Eureka

---

### **4. Persistence Layer (MongoDB)**

**Responsabilidade:** Persistência de dados no MongoDB

#### **Modelo de Dados (Document)**

```java
@Document(collection = "ordens_servico")
public class OrdemServico {
    @Id
    private String id;  // ObjectId do MongoDB
    
    @Field("status")
    private StatusOrdemServico status;
    
    @Field("data_criacao")
    private LocalDateTime dataCriacao;
    
    // Referências (não relacionamentos JPA)
    @Field("cliente_id")
    private Long clienteId;
    
    @Field("veiculo_id")
    private Long veiculoId;
    
    @Field("mecanico_id")
    private Long mecanicoId;
    
    @Field("servicos_ids")
    private List<Long> servicosIds;
    
    // Subdocumentos embarcados
    private List<ItemOrdemServico> itensOrdemServico;
}
```

**Repository:**
```java
@Repository
public interface OrdemServicoRepository 
    extends MongoRepository<OrdemServico, String> {
    
    List<OrdemServico> findByClienteId(Long clienteId);
    List<OrdemServico> findByVeiculoId(Long veiculoId);
    List<OrdemServico> findByStatusIn(List<StatusOrdemServico> status);
}
```

---

## 🔄 Fluxo de Criação de OS

```mermaid
sequenceDiagram
    participant C as Cliente (UI)
    participant WO as Work Order Service
    participant CS as Customer Service
    participant M as MongoDB
    participant E as Event Bus

    C->>WO: POST /api/ordens-servico
    WO->>CS: GET /api/clientes/{id} (Feign)
    CS-->>WO: ClienteResponseDTO
    WO->>CS: GET /api/veiculos/{id} (Feign)
    CS-->>WO: VeiculoResponseDTO
    WO->>M: save(OrdemServico)
    M-->>WO: OrdemServico (ObjectId)
    WO->>E: publish(CalcularOrcamentoEvent)
    WO-->>C: OrdemServicoResponseDTO (dados enriquecidos)
```

---

## 📦 Estrutura de Dados

### **Normalização vs Desnormalização**

O Work Order Service utiliza uma abordagem **híbrida**:

#### **Dados Normalizados (Referências)**
- `clienteId`: Referência ao customer-service
- `veiculoId`: Referência ao customer-service
- `mecanicoId`: Referência ao auth-service
- `servicosIds`: Lista de referências ao catalog-service

**Vantagem:** Dados sempre atualizados via Feign Client

#### **Dados Desnormalizados (Embarcados)**
- `itensOrdemServico`: Subdocumentos com produtos e quantidades

**Vantagem:** Leitura rápida sem joins

---

## 🔐 Segurança

### **Autenticação**
- JWT Token (fornecido pelo auth-service)
- Validação via `JwtAuthenticationFilter`

### **Autorização**
```java
@PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
public ResponseEntity<OrdemServicoResponseDTO> criar(OsRequestDTO request)

@PreAuthorize("hasRole('MECANICO')")
public ResponseEntity<OrdemServicoResponseDTO> executar(Long id)
```

**Roles:**
- `ADMIN`: Acesso total
- `ATENDENTE`: Criar, atualizar, gerenciar OS
- `MECANICO`: Executar, finalizar OS
- `CLIENTE`: Visualizar apenas suas próprias OS

---

## 🌐 Configuração de Ambientes

### **Local (Profile: local)**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://user:pass@cluster.mongodb.net/workorder_db

customer-service:
  url: http://localhost:8081

catalog-service:
  url: http://localhost:8083

eureka:
  client:
    enabled: false
```

### **Kubernetes (Profile: default)**
```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}

eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://eureka-service:8761/eureka/
```

---

## 📊 Comparação com V1 (PostgreSQL)

| Aspecto | V1 (PostgreSQL) | V2 (MongoDB) |
|---------|-----------------|--------------|
| **ID** | Long (auto-increment) | String (ObjectId) |
| **Persistência** | JPA/Hibernate | Spring Data MongoDB |
| **Relacionamentos** | Foreign Keys + JOINs | Referências + Feign |
| **Transações** | ACID via @Transactional | Não usa transações |
| **Dados cliente/veículo** | JOIN no SQL | Feign Client (HTTP) |
| **Escalabilidade** | Vertical | Horizontal (Sharding) |
| **Schema** | Rígido (migrations) | Flexível |

---

## 🚀 Decisões Técnicas

### **Por que MongoDB?**

1. **Escalabilidade Horizontal:** Suporta sharding nativo para crescimento
2. **Schema Flexível:** Facilita adição de novos campos sem migrations
3. **Performance em Leituras:** Índices eficientes e agregações rápidas
4. **Cloud-Ready:** MongoDB Atlas gerenciado e otimizado
5. **Demonstração Técnica:** Requisito da Fase 4 (SQL + NoSQL)

### **Por que Feign Client?**

1. **Desacoplamento:** Serviços independentes
2. **Dados Atualizados:** Sempre busca dados mais recentes
3. **Resiliência:** Circuit breaker e retry automáticos
4. **Simplicidade:** Interface declarativa

### **Trade-offs**

**Vantagens:**
- ✅ Dados sempre atualizados (tempo real)
- ✅ Escalabilidade horizontal
- ✅ Schema flexível
- ✅ Performance em leituras

**Desvantagens:**
- ⚠️ Latência adicional (chamadas HTTP)
- ⚠️ Dependência de outros serviços
- ⚠️ Complexidade de monitoramento distribuído

---

## 📈 Observabilidade

### **Métricas (Actuator)**
- `/actuator/health` - Status do serviço
- `/actuator/metrics` - Métricas de performance
- `/actuator/info` - Informações do serviço

### **Logs**
```java
log.info("Criando ordem de serviço para cliente ID: {} e veículo ID: {}",
         request.getClienteId(), request.getVeiculoId());
```

### **Eventos de Domínio**
- `CalcularOrcamentoEvent` - Após criar/diagnosticar OS
- `VeiculoDisponivelEvent` - Após finalizar OS

---

## 🔄 Próximas Melhorias

### **Curto Prazo**
- [ ] Restaurar Security JWT completo
- [ ] Implementar cache com Redis
- [ ] Adicionar retry policy no Feign

### **Médio Prazo**
- [ ] Implementar Saga Pattern completo (compensação)
- [ ] Adicionar mensageria (RabbitMQ/Kafka)
- [ ] Implementar CQRS (Command/Query Separation)

### **Longo Prazo**
- [ ] Event Sourcing para auditoria
- [ ] API GraphQL para queries flexíveis
- [ ] Machine Learning para estimativa de tempo/custo

---

## 📚 Referências

- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [MongoDB Best Practices](https://www.mongodb.com/docs/manual/applications/data-models/)
- [Microservices Patterns](https://microservices.io/patterns/index.html)

---

**Tech Challenge - Fase 4 | FIAP SOAT | Grupo 36**
