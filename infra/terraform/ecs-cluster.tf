resource "aws_ecs_cluster" "main" {
  name = "${local.name_prefix}-cluster"

  configuration {
    execute_command_configuration {
      logging = "OVERRIDE"

      log_configuration {
        cloud_watch_encryption_enabled = true
        cloud_watch_log_group_name     = aws_cloudwatch_log_group.ecs_exec.name
      }
    }
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-cluster"
  })
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name = aws_ecs_cluster.main.name

  capacity_providers = ["FARGATE"]

  # 클러스터에 기본적으로 사용할 용량 공급자 전략
  default_capacity_provider_strategy {
    base              = 1   # 지정된 용량 공급자에서 실행할 최소 작업 수
    weight            = 100 # 지정된 용량 공급자를 사용해야 하는 전체 실행된 작업 수의 상대적 백분율
    capacity_provider = "FARGATE"
  }
}