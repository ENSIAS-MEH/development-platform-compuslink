# Security group for RDS (only accessible from EKS nodes)
resource "aws_security_group" "rds" {
  name   = "${var.project_name}-rds-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_eks_cluster.main.vpc_config[0].cluster_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-rds-sg" }
}

# DB Subnet Group
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-db-subnet"
  subnet_ids = aws_subnet.private[*].id

  tags = { Name = "${var.project_name}-db-subnet" }
}

# Single RDS instance (7 databases created via init script in k8s)
resource "aws_db_instance" "postgres" {
  identifier     = "${var.project_name}-postgres"
  engine         = "postgres"
  engine_version = "16.3"
  instance_class = var.db_instance_class

  allocated_storage     = 20
  max_allocated_storage = 50
  storage_type          = "gp3"

  db_name  = "compuslink_users"
  username = var.db_username
  password = var.db_password

  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name

  multi_az            = false
  publicly_accessible = false
  skip_final_snapshot = true

  backup_retention_period = 7

  tags = { Name = "${var.project_name}-postgres" }
}
