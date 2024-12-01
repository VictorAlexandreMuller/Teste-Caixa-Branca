package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// Classe responsavel por realizar a conexao com o banco de dados e verificar usuarios.
public class User {

    /**
     * Metodo responsavel por estabelecer a conexao com o banco de dados.
     * 
     * @return Connection Objeto de conexão com o banco de dados, ou null caso ocorra algum erro.
     */
    public Connection conectarBD() {
        Connection conn = null; // Inicializa a conexao como nula.
        try {
            // Carrega o driver do MySQL e cria uma instancia.
            Class.forName("com.mysql.Driver").newInstance();
            
            // String de conexao com o banco de dados.
            String url = "jdbc:mysql://127.0.0.1/test?user=lopes&password=123";
            
            // Estabelece a conexao.
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            // Se ocorrer algum erro, a conexao permanecera nula.
        }
        return conn; // Retorna a conexao (ou null).
    }

    // Nome do usuario, preenchido caso a verificacao seja bem sucedida.
    public String nome = "";

    // Indica se o login foi verificado com sucesso.
    public boolean result = false;

    /**
     * Metodo para verificar se um usuario existe no banco de dados.
     * 
     * @param login Login do usuario a ser verificado.
     * @param senha Senha do usuario a ser verificada.
     * @return boolean Retorna true se o usuario for encontrado, ou false caso contrario.
     */
    public boolean verificarUsuario(String login, String senha) {
        String sql = ""; // Inicializa a variavel de instrucao SQL.
        
        // Chama o metodo de conexao com o banco.
        Connection conn = conectarBD();
        
        // Monta a instrucao SQL para buscar o usuario no banco.
        sql += "select nome from usuarios ";
        sql += "where login = '" + login + "' ";
        sql += "and senha = '" + senha + "';";
        
        try {
            // Cria um Statement para executar a instrucao SQL.
            Statement st = conn.createStatement();
            
            // Executa a consulta e recebe o resultado.
            ResultSet rs = st.executeQuery(sql);
            
            // Se o usuario for encontrado, atualiza o resultado e o nome.
            if (rs.next()) {
                result = true; // Login válido.
                nome = rs.getString("nome"); // Obtem o nome do usuario.
            }
        } catch (Exception e) {
            // Em caso de erro, o resultado permanece como falso.
        }
        return result; // Retorna o resultado da verificacao.
    }
}
