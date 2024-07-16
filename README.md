# OrderManager

## Overview

OrderManager é uma aplicação de gerenciamento de pedidos onde usuários podem criar e gerenciar pedidos. Os itens podem ser encomendados e os pedidos são automaticamente atendidos assim que o estoque permite.

## Features

- Criar, ler, atualizar, deletar e listar todas as entidades.
- Quando um pedido é criado, o sistema tenta atendê-lo com o estoque atual.
- Quando um movimento de estoque é criado, o sistema tenta atribuí-lo a um pedido que não esteja completo.
- Quando um pedido é completo, uma notificação por e-mail é enviada ao usuário que o criou.
- Rastrear a lista de movimentos de estoque que foram usados para completar o pedido, e vice-versa.
- Mostrar o progresso atual de cada pedido.
- Escrever um arquivo de log com: pedidos completos, movimentos de estoque, e-mails enviados e erros.

## Entidades

- **Item**
- name
- quantity

- **StockMovement**
- creationDate
- item
- quantity

- **Order**
- creationDate
- item
- quantity
- fulfilledQuantity
- completed
- user (quem criou o pedido)

- **User**
- name
- email

## Requisitos

- Java 8 + Java EE + Hibernate, PostgreSQL, GIT, log4j (ou outro)

## Configuração do Projeto

### Configurações do Banco de Dados

```sql -- Create the Item table      
CREATE TABLE items (      
    id SERIAL PRIMARY KEY,      
    name VARCHAR(255) NOT NULL,      
    quantity INTEGER NOT NULL DEFAULT 0      
);      
      
-- Create the AppUser table      
CREATE TABLE app_user (      
    id SERIAL PRIMARY KEY,      
    name VARCHAR(255) NOT NULL,      
    email VARCHAR(255) NOT NULL UNIQUE      
);      
      
-- Create the Order table      
CREATE TABLE orders (      
    id SERIAL PRIMARY KEY,      
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,      
    item_id INTEGER NOT NULL,      
    quantity INTEGER NOT NULL,      
    fulfilled_quantity INTEGER NOT NULL DEFAULT 0,      
    completed BOOLEAN NOT NULL DEFAULT FALSE,      
    user_id INTEGER NOT NULL,      
    FOREIGN KEY (item_id) REFERENCES items (id),      
    FOREIGN KEY (user_id) REFERENCES app_user (id)      
);      
      
-- Create the StockMovement table      
CREATE TABLE stock_movement (      
    id SERIAL PRIMARY KEY,      
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,      
    item_id INTEGER NOT NULL,      
    quantity INTEGER NOT NULL,      
    FOREIGN KEY (item_id) REFERENCES items (id)      
);  
```   
### Executando o Projeto

- Clone o repositório:
   ```sh      
   git clone <URL do repositório>      
   cd OrderManager  
   ```   
- Configure o banco de dados PostgreSQL conforme as instruções acima.

- Atualize as configurações de conexão com o banco de dados, do logger e de servidor smtp do gmail no arquivo application.properties.

  -- Abra o arquivo `src/main/resources/application.properties` e adicione ou atualize as seguintes propriedades conforme necessário:

  ```properties  
  server.port=8081  
    
  spring.datasource.url=jdbc:postgresql://localhost:5432/ordermanager  
  spring.datasource.username=postgres  
  spring.datasource.password=postgres  
  spring.datasource.driver-class-name=org.postgresql.Driver  
    
  spring.jpa.hibernate.ddl-auto=update  
  spring.jpa.show-sql=true  
    
  logging.file.name=order_manager.log  
  logging.level.org.hibernate.SQL=DEBUG  
  logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE  
    
  spring.mail.host=smtp.gmail.com  
  spring.mail.port=587  
  spring.mail.username=<SEU_EMAIL@gmail.com>  
  spring.mail.password=<SENHA_DE_APLICATIVO>  
  spring.mail.properties.mail.smtp.auth=true  
  spring.mail.properties.mail.smtp.starttls.enable=true  
  spring.mail.properties.mail.smtp.connectiontimeout=5000  
  spring.mail.properties.mail.smtp.timeout=5000  
  spring.mail.properties.mail.smtp.writetimeout=5000  
  ```  

- `<SEU_EMAIL@gmail.com>`: O endereço de e-mail do Gmail para enviar as notificações.
- `<SENHA_DE_APLICATIVO>`: Senha de aplicativo gerada para acessar o servidor de e-mail.

Para configurar o envio de e-mails usando uma conta Google, você precisa gerar uma senha de aplicativo. Siga os passos abaixo para gerar a senha:

1. **Acesse a Conta Google:**
- Faça login na sua conta Google.

2. **Acesse as Configurações de Segurança:**
- Clique na sua foto de perfil no canto superior direito e selecione "Gerenciar sua Conta Google".
- No menu à esquerda, clique em "Segurança".

3. **Habilitar a Verificação em Duas Etapas:**
- Em "Como fazer login no Google", certifique-se de que a "Verificação em duas etapas" está ativada.
- Se não estiver ativada, siga as instruções na tela para ativá-la.

4. **Gerar Senha de Aplicativo:**
- Após habilitar a verificação em duas etapas, volte para a página de "Segurança".
- Na seção "Fazer login no Google", clique em "Senhas de app".
- Faça login novamente se solicitado.
- Na página "Senhas de app", selecione "Outro (nome personalizado)" no menu suspenso.
- Digite um nome descritivo, como "OrderManagerApp", e clique em "Gerar".

5. **Obter a Senha de Aplicativo:**
- Uma janela pop-up aparecerá com a senha de aplicativo gerada.
- Copie esta senha. Você precisará dela para configurar o `spring.mail.password` em seu arquivo `application.properties`.

- Compile e execute o projeto:

   ```sh      
   mvn clean install      
   mvn spring-boot:run  
   ```   
### Chamadas HTTP

#### Stock Movement

- **Criar Stock Movement**
- Método: POST
- URL: `http://localhost:8081/api/stockMovements`
- Payload:
  ```json      
 {      
       "item": {      
         "id": 1      
       },      
       "quantity": 25      
     }      
 ```   
- **Listar Stock Movements**
- Método: GET
- URL: `http://localhost:8081/api/stockMovements`

- **Obter Stock Movement por ID**
- Método: GET
- URL: `http://localhost:8081/api/stockMovements/{id}`

- **Atualizar Stock Movement**
- Método: PUT
- URL: `http://localhost:8081/api/stockMovements/{id}`
- Payload:
  ```json      
 {      
       "item": {      
         "id": 1      
       },      
       "quantity": 30      
     }      
 ```   
- **Deletar Stock Movement**
- Método: DELETE
- URL: `http://localhost:8081/api/stockMovements/{id}`

#### User

- **Criar User**
- Método: POST
- URL: `http://localhost:8081/api/users`
- Payload:
  ```json      
 {      
       "name": "User Name",      
       "email": "user@example.com"      
     }      
 ```   
- **Listar Users**
- Método: GET
- URL: `http://localhost:8081/api/users`

#### Item

- **Criar Item**
- Método: POST
- URL: `http://localhost:8081/api/items`
- Payload:
  ```json      
 {      
       "name": "Sample Item"      
     }      
 ```   
- **Listar Items**
- Método: GET
- URL: `http://localhost:8081/api/items`

- **Atualizar Item**
- Método: PUT
- URL: `http://localhost:8081/api/items/{id}`
- Payload:
  ```json      
 {      
       "name": "Updated Item Name"      
     }      
 ```   
- **Deletar Item**
- Método: DELETE
- URL: `http://localhost:8081/api/items/{id}`

#### Order

- **Criar Order**
- Método: POST
- URL: `http://localhost:8081/api/orders`
- Payload:
  ```json      
 {      
       "item": {      
         "id": 1      
       },      
       "quantity": 5,      
       "user": {      
         "id": 1      
       }      
     }      
 ```   
- **Listar Orders**
- Método: GET
- URL: `http://localhost:8081/api/orders`

- **Obter Order por ID**
- Método: GET
- URL: `http://localhost:8081/api/orders/{id}`

- **Atualizar Order**
- Método: PUT
- URL: `http://localhost:8081/api/orders/{id}`
- Payload:
  ```json      
 {      
       "quantity": 10,      
       "fulfilledQuantity": 5,      
       "completed": false,      
       "item": {      
         "id": 1      
       },      
       "user": {      
         "id": 1      
       }      
     }      
 ```   
- **Deletar Order**
- Método: DELETE
- URL: `http://localhost:8081/api/orders/{id}`

### Testing with Postman

Você pode testar as rotas usando o Postman. Importe a coleção Postman incluída no arquivo `OrderManager.postman_collection.json`.

Para importar a coleção no Postman:

1. Abra o Postman.
2. Clique em "Import" no canto superior esquerdo.
3. Selecione "File" e escolha o arquivo `OrderManager.postman_collection.json`.
4. Clique em "Import".