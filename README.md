# b-eletricar
## Visão do projeto
O B-Eletricar é um projeto desenvolvido com o objetivo de atender às curiosidades de amantes e entusiastas de carros elétricos sobre as especificações técnicas dos veículos disponíveis no mercado e as diferenças entre eles. A plataforma disponibiliza um catálogo de automóveis com informações como marca, modelo, carroceria, preço, autonomia, capacidade da bateria e potência, permitindo que os usuários conheçam e analisem diferentes modelos. Além disso, o sistema oferece a possibilidade de comparar veículos de diferentes fabricantes, facilitando a identificação de seus pontos fortes e fracos e auxiliando o usuário na escolha de um automóvel que melhor atenda às suas necessidades e condições financeiras. O projeto também prevê um sistema de avaliações, no qual os usuários poderão compartilhar suas experiências e opiniões sobre os veículos, contribuindo para a troca de informações entre os entusiastas.

## Descrições técnicas do site
O B-Eletricar utiliza Spring Boot (versão 4.1.1) como estrutura principal para o desenvolvimento da aplicação, em conjunto com Thymeleaf para a construção das páginas web. O PostgreSQL é utilizado como sistema gerenciador de banco de dados, enquanto o Docker Compose auxilia na configuração e execução dos serviços necessários ao projeto. 

| Tecnologia | Função
| --- | --- 
| **Java** | Linguagem de programação utilizada no desenvolvimento da aplicação.
| **Spring Boot** | Framework responsável pela estrutura da aplicação, gerenciamento de dependências e disponibilização dos recursos web.
| **Thymeleaf** | Motor de templates utilizado para renderizar as páginas HTML dinamicamente a partir dos dados fornecidos pela aplicação.
| **PostgreSQL** | Sistema de Gerenciamento de Banco de Dados (SGBD) relacional utilizado para armazenar as informações dos veículos e demais dados do sistema.
| **Docker Compose** | Ferramenta utilizada para configurar e executar os serviços da aplicação por meio de contêineres.

## Implementação

A implementação do projeto é organizada em camadas, buscando separar as responsabilidades e facilitar a manutenção e evolução do sistema -

- Model: representa as entidades do sistema e suas relações com o banco de dados;
- Repository: responsável pelo acesso e pela comunicação com o banco de dados, utilizando os recursos do Spring Data JPA.;
- Service: concentra as regras de negócio e o processamento das operações solicitadas pela aplicação;
- Controller: recebe as requisições HTTP, aciona os serviços correspondentes e direciona as respostas para as páginas da aplicação;
- View: composta pelos templates HTML desenvolvidos com Thymeleaf, responsáveis pela apresentação das informações ao usuário.

## Funcionalidades

As funcionalidades do sistema são organizadas de acordo com os diferentes tipos de usuários e suas permissões.

### Visitante -

- Visualizar o catálogo de veículos elétricos;
- Consultar as especificações de um veículo;
- Pesquisar e filtrar veículos de acordo com suas características.

### Usuário cadastrado -

- Realizar todas as funcionalidades do visitante (herança);
- Comparar diferentes modelos.
- Realizar avaliações dos veículos;

### Administrador -

- Gerenciar os veículos e as informações disponibilizadas no catálogo;
- Gerenciar os demais dados necessários ao funcionamento da plataforma.

## Requisitos e como rodar

Docker com Compose v2. Para rodar pela IDE, também JDK 21.

## 1. Arquivo `.env`

Crie um `.env` na raiz do projeto (mesma pasta do `compose.yaml`):

```env
POSTGRES_DB=beletricar
POSTGRES_USER=beletricar
POSTGRES_PASSWORD=beletricar
GOOGLE_CLIENT_ID=codigo_aqui
GOOGLE_CLIENT_SECRET=segredo_do_codigo_aqui
```
O .env impede que envie para o repositorio remoto
**Por que está exposto aqui:** o `.env` está no `.gitignore`, então não vai para o repositório, e sem ele o `compose.yaml` não tem as variáveis do app. Como o projeto é acadêmico, os valores são apenas de desenvolvimento e não protegem nada real, e deixá-los no README permite que qualquer colega ou professor rode o projeto sem pedir nada.

## 2. Subir

```bash
docker compose up --build -d
docker compose logs -f app     # aguarde "Started BEletricarApplication"
```

As tabelas são criadas automaticamente pelo Hibernate quando o app sobe. O site fica em http://localhost:8080.

## 3. Reiniciar

```bash
docker compose restart app          # reinicia só o app
docker compose up --build -d        # após alterar o código
docker compose down                 # para tudo (mantém os dados)
docker compose down -v              # para tudo e APAGA o banco
```

## 4. Preencher o banco

Só depois de o app ter subido (é ele que cria as tabelas):

```bash
docker compose exec -T db psql -U beletricar -d beletricar < database/popular_banco.sql
```

No PowerShell, o `<` não funciona; use:

```powershell
Get-Content database/popular_banco.sql -Raw | docker compose exec -T db psql -U beletricar -d beletricar
```

Insere 9 fornecedores, 4 usuários e 21 veículos. **Rode uma única vez**: repetir duplica os fornecedores e gera erros. Para refazer, use `docker compose down -v`, suba de novo e rode o script.

Logins criados pelo script: `admin@beletricar.com` / `admin123` e, como usuário comum, `ana.souza@exemplo.com` / `user1234`.

## 5. Desenvolvimento pela IDE

O banco continua no Docker; só o app roda na IDE.

```bash
docker compose stop app        # libera a porta 8080 (se o app estiver no Docker)
docker compose up -d db
```
Em application-docker.properties
    rodando com ide -> spring.datasource.url=jdbc:postgresql://localhost:5432/beletricar
    rodando com app e db no docker -> spring.datasource.url=jdbc:postgresql://db:5432/beletricar
Execute `BEletricarApplication` com estas variáveis de ambiente na configuração de execução:

Ou pelo terminal: `./mvnw spring-boot:run` (com as variáveis exportadas).

**Não ative o perfil `docker`** na IDE. Ele aponta para o host `db`, que só existe dentro da rede do Compose. Sem perfil, o app usa `localhost:5432`, que funciona porque o `compose.yaml` publica essa porta.