terraform {
  backend "s3" {
    bucket = "fiap-fase-4-oficina"
    key    = "work-order-service/terraform.tfstate"
    region = "us-east-1"
  }
}
