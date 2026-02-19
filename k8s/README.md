# Kubernetes Manifests - work-order-service

Estrutura padronizada por servico:

- \'k8s/base\': Deployment, Service, ConfigMap, Secret e HPA
- \'k8s/overlays/local\': deploy local (NodePort + imagem local)
- \'k8s/overlays/aws\': deploy em AWS (ClusterIP + pull Always)
- \'k8s/overlays/localstack\': deploy com endpoint AWS apontando para LocalStack

## Observacao sobre o Eureka

Todos os servicos (exceto o proprio \'eureka-service\') possuem \'initContainer\' aguardando o \'eureka-service:8761\' antes de subir. Isso garante que o registro no discovery funcione no bootstrap.

## Aplicar

```bash
# Local
kubectl apply -k k8s/overlays/local

# AWS
kubectl apply -k k8s/overlays/aws

# LocalStack
kubectl apply -k k8s/overlays/localstack
```
