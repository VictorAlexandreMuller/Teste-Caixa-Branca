# Teste-Caixa-Branca

# Parte I: Problemas Encontrados no Código

A seguir serão descritos todos os problemas encontrados no código para a classe `User`, que implementam funcionalidades de conexão a um banco de dados MySQL e verificação de login de usuários.

Os problemas estão divididos em tópicos, e as respectivas soluções aplicadas também estão documentadas.

## **1. Driver Depreciado**

- **Descrição do Problema:**
  O driver utilizado no código original (`com.mysql.Driver`) está depreciado em versões recentes do MySQL Connector e pode não funcionar corretamente em ambientes modernos.

  O driver `com.mysql.Driver` foi substituído por `com.mysql.cj.jdbc.Driver` em versões mais recentes do MySQL Connector. O uso do driver antigo pode gerar incompatibilidades.

- **Linha com Problema:**

  ```java
  Class.forName("com.mysql.Driver").newInstance();
  ```

- **Solução:**
  Atualizar para o driver mais recente:
  ```java
  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
  ```

## **2. Conexões Não Fechadas**

- **Descrição do Problema:**
  O código não fecha os recursos como `Connection`, `Statement` e `ResultSet`. Isso pode causar vazamento de conexões, reduzindo o desempenho e a escalabilidade do sistema.

  Recursos abertos e não fechados consomem memória e podem esgotar o pool de conexões do banco de dados, comprometendo o funcionamento do sistema.

- **Linha com Problema:**

  ```java
  Connection conn = conectarBD();
  Statement st = conn.createStatement();
  ResultSet rs = st.executeQuery(sql);
  ```

- **Solução:**
  Utilizar `try-with-resources`, que garante o fechamento automático desses recursos:

  ```java
  try (Connection conn = conectarBD();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
  }
  ```

  ## **3. SQL Injection**

- **Descrição do Problema:**
  A concatenação de strings SQL para incluir os parâmetros `login` e `senha` deixa o sistema vulnerável a ataques de SQL Injection.

- **Exemplo do Problema:**

  ```java
  sql += "where login = '" + login + "' ";
  sql += "and senha = '" + senha + "';";
  ```

- **Solução:**
  Substituir `Statement` por `PreparedStatement`, utilizando placeholders para os parâmetros:

  ```java
  String sql = "SELECT nome FROM usuarios WHERE login = ? AND senha = ?";
  ps.setString(1, login);
  ps.setString(2, senha);
  ```

  ## **4. Variáveis Globais Desnecessárias**

- **Descrição do Problema:**
  As variáveis `nome` e `result` foram declaradas como globais na classe. Isso pode causar problemas de concorrência em ambientes multi-threaded.

- **Solução:**
  Transformar essas variáveis em variáveis locais dentro do método `verificarUsuario`:

  ```java
  boolean result = false;
  String nome = "";
  ```

  ## **5. Captura de Exceções Vazia**

- **Descrição do Problema:**
  O código original captura exceções, mas não fornece informações sobre o erro. Isso dificulta a depuração.

- **Exemplo do Problema:**

  ```java
  catch (Exception e) { }
  ```

- **Solução:**
  Adicionar um log da exceção, como `e.printStackTrace()`:

  ```java
  catch (Exception e) {
    e.printStackTrace();
  }
  ```

  ## **6. Ineficiência na Concatenação de Strings**

- **Descrição do Problema:**
  O código original utiliza várias concatenações de strings (`+=`) para montar a instrução SQL, o que é ineficiente e desnecessário.

- **Solução:**
  Escrever a instrução SQL como uma única string:

  ```java
  String sql = "SELECT nome FROM usuarios WHERE login = ? AND senha = ?";
  ```

  ## **7. Estrutura de Conexão Antiga**

- **Descrição do Problema:**
  O método `Class.forName()` para carregar o driver pode não ser necessário em configurações modernas, já que o `DriverManager` é capaz de carregar automaticamente o driver adequado.

- **Solução:**
  Avaliar a necessidade de `Class.forName()` e removê-lo, se for redundante:
  ```java
  // Apenas criar a conexão diretamente:
  Connection conn = DriverManager.getConnection(url);
  ```

---

---

# Parte II: Grafo de Fluxo

Um Grafo de Fluxo é usada para modelar o fluxo de controle dentro de um programa. Ele ajuda a identificar os caminhos que o código pode seguir durante sua execução, destacando pontos de decisão, loops e o fluxo lógico do programa.

## **1. Identificação dos pontos de fluxos**

### **Enumeração dos pontos importantes no fluxo de execução:**

### Método `conectarBD`;

1- Início do método; </br>
2- Inicialização de `conn` como `null`;</br>
3- Tentativa de carregar o driver e conectar ao banco (bloco `try`);</br>
4- Conexão obtida com sucesso;</br>
5- Exceção capturada (bloco `catch`);</br>
6- Retorno do objeto `conn`;</br>

### Método `verificarUsuario`;

7- Início do método;</br>
8- Chamada ao método `conectarBD`;</br>
9- Montagem da instrução SQL;</br>
10- Execução da consulta SQL (bloco `try`);</br>
11- Usuário encontrado (condição `if` no ResultSet);</br>
12- Exceção capturada (bloco `catch`);</br>
13- Retorno do resultado (`result`).</br>

## **2. Grafo de fluxo**

Grafo de controle, considerando os fluxos possíveis entre os pontos:

### Método `conectarBD`: </br>

1 -> 2 -> 3 -> (4 ou 5) -> 6

### Método `verificarUsuario`: </br>

7 -> 8 -> 9 -> 10 -> (11 ou 12) -> 13

### Complexidade Ciclomática:

Método `conectarBD`: 2 </br>
Método `verificarUsuario`: 3

#### Método `conectarBD`: </br>

1 → 2 → 3 → 4 → 6 </br>
1 → 2 → 3 → 5 → 6 </br>

### Sequências de Caminhos

#### Método `conectarBD`: </br>

1 → 2 → 3 → 4 → 6 </br>
1 → 2 → 3 → 5 → 6 </br>

#### Método `verificarUsuario`: </br>

7 → 8 → 9 → 10 → 11 → 13 </br>
7 → 8 → 9 → 10 → 12 → 13 </br>

### Grafo Graficamente Representado

- Nodos: Cada ponto numerado é um nodo.
- Arestas: São as transições entre os nodos.

#### Grafo de Controle - Método `conectarBD`:

![](https://imgur.com/8BONzZ6.png)

#### Grafo de Controle - Método `verificarUsuario`:

![](https://imgur.com/aeyXg4z.png)

## **3. Complexidade Ciclomática**

Grafo de controle, considerando os fluxos possíveis entre os pontos:

Fórmula: V(G) = E − N + 2

E: Número de arestas. </br>
N: Número de nodos. </br>

#### Para o método `conectarBD`:

Nodos (N): 6 </br>
Arestas (E): 6 </br>

V(G) = 6 − 6 + 2 = 2

#### Para o método `verificarUsuario`:

Nodos (N): 7 </br>
Arestas (E): 8 </br>

V(G) = 8 − 7 + 2 = 3

---
