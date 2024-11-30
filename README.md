# Teste-Caixa-Branca

# Problemas Encontrados no Código

Este documento descreve todos os problemas encontrados no código inicial fornecido para a classe `User`, que implementa funcionalidades de conexão a um banco de dados MySQL e verificação de login de usuários. Os problemas estão divididos em tópicos, e as respectivas soluções aplicadas também foram documentadas.

---

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
