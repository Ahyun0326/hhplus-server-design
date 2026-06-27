module "registry" {
  source = "./modules/registry"

  ecr_repository_name = var.ecr_repository_name
  common_tags         = local.common_tags
}

module "observability" {
  source = "./modules/observability"

  project_name = var.project_name
  environment  = var.environment
  name_prefix  = local.name_prefix
  common_tags  = local.common_tags
}

module "iam" {
  source = "./modules/iam"

  name_prefix = local.name_prefix
  common_tags = local.common_tags
}

module "ecs_cluster" {
  source = "./modules/ecs-cluster"

  name_prefix             = local.name_prefix
  ecs_exec_log_group_name = module.observability.ecs_exec_log_group_name
  common_tags             = local.common_tags
}

module "network" {
  source = "./modules/network"

  name_prefix          = local.name_prefix
  vpc_cidr             = var.vpc_cidr
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  common_tags          = local.common_tags
}

module "security" {
  source = "./modules/security"

  name_prefix = local.name_prefix
  vpc_id      = module.network.vpc_id
  app_port    = var.app_port
  common_tags = local.common_tags
}

module "vpc_endpoints" {
  source = "./modules/vpc-endpoints"

  aws_region                  = var.aws_region
  name_prefix                 = local.name_prefix
  vpc_id                      = module.network.vpc_id
  private_subnet_ids          = module.network.private_subnet_ids
  private_route_table_id      = module.network.private_route_table_id
  vpc_endpoint_security_group = module.security.vpc_endpoint_security_group_id
  common_tags                 = local.common_tags
}

module "load_balancer" {
  source = "./modules/load-balancer"

  name_prefix           = local.name_prefix
  public_subnet_ids     = module.network.public_subnet_ids
  alb_security_group_id = module.security.alb_security_group_id
  vpc_id                = module.network.vpc_id
  app_port              = var.app_port
  common_tags           = local.common_tags
  certificate_arn       = module.domain.acm_certificate_arn
}

module "domain" {
  source = "./modules/domain"

  name_prefix = local.name_prefix
  domain_name = var.domain_name
  common_tags = local.common_tags
}

module "dns_record" {
  source = "./modules/dns-record"

  hosted_zone_id = module.domain.hosted_zone_id
  domain_name    = var.domain_name
  api_subdomain  = var.api_subdomain
  alb_dns_name   = module.load_balancer.alb_dns_name
  alb_zone_id    = module.load_balancer.alb_zone_id
}
