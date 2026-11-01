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
- Comparar diferentes modelos.

### Usuário cadastrado -

- Realizar todas as funcionalidades do visitante (herança);
- Realizar avaliações dos veículos;
- Compartilhar opiniões e experiências com outros usuários;

### Administrador -

- Gerenciar os veículos e as informações disponibilizadas no catálogo;
- Gerenciar os demais dados necessários ao funcionamento da plataforma.
