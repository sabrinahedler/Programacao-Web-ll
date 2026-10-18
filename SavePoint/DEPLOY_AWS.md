# Deploy do SavePoint na AWS (EC2 + Docker Compose)

Uma única instância EC2 roda a API e o MySQL em containers. É o caminho mais simples e barato para o trabalho.

## 1. Criar a instância EC2
1. Console AWS → região **São Paulo (sa-east-1)** → EC2 → **Launch instance**.
2. Nome: `savepoint-api`. Imagem: **Ubuntu Server 24.04 LTS**.
3. Tipo: **t3.small** (2 GB). Na t3.micro (1 GB) a API + MySQL ficam no limite de memória.
4. Key pair: criar `savepoint-key` (.pem) e guardar o arquivo.
5. Security group (regras de entrada):
   - SSH (22) → **My IP**
   - HTTP (80) → **Anywhere (0.0.0.0/0)**
6. Storage: 20 GB gp3. **Launch instance**.

## 2. Instalar Docker na instância
```bash
ssh -i savepoint-key.pem ubuntu@IP_PUBLICO
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker ubuntu && exit
```
(Reconectar via SSH para valer o grupo docker.)

## 3. Enviar o projeto
Da sua máquina, na pasta que contém `SavePoint/`:
```bash
scp -i savepoint-key.pem -r SavePoint ubuntu@IP_PUBLICO:~/
```
(ou `git clone` do repositório, se estiver no GitHub)

## 4. Configurar segredos
```bash
cd ~/SavePoint
cp .env.example .env
openssl rand -base64 48   # copiar o resultado para JWT_SECRET
nano .env                 # preencher JWT_SECRET, MYSQL_ROOT_PASSWORD, DB_PASSWORD, ADMIN_EMAIL, ADMIN_PASSWORD
```

## 5. Subir
```bash
docker compose -f docker-compose.prod.yml up -d --build
docker compose -f docker-compose.prod.yml logs -f api   # aguardar "Started SavepointprojectApplication"
```

## 6. Testar
- Health: `http://IP_PUBLICO/actuator/health` → `{"status":"UP"}`
- Swagger: `http://IP_PUBLICO/swagger-ui.html`
- Fluxo: `POST /auth/register` → `POST /auth/login` → copiar o token → botão **Authorize** no Swagger → chamar rotas protegidas.

## Custos
A instância cobra por hora ligada. Depois da apresentação: EC2 → Instance state → **Stop** (para) ou **Terminate** (apaga).

## Alternativa: Elastic Beanstalk + RDS
A aplicação já lê tudo por variável de ambiente e respeita a variável `PORT` (Beanstalk usa 5000).
Basta gerar o jar (`./mvnw package -DskipTests` → `target/savepoint.jar`), criar um RDS MySQL 8 e definir no
ambiente do Beanstalk: `SPRING_PROFILES_ACTIVE=prod`, `DB_URL=jdbc:mysql://<endpoint-rds>:3306/savepointdb?createDatabaseIfNotExist=true`,
`DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`. Health check path: `/actuator/health`.
