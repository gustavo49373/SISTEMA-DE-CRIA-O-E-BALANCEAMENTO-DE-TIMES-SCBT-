package projetoPOO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nome;
    private String username;
    private String senha;
    private boolean ativo;

    private static final List<Usuario> usuariosCadastrados = new ArrayList<>();

    public Usuario(String nome, String username, String senha){
        this.nome = nome;
        this.username = username;
        this.senha = gerarHash(senha);
        this.ativo = true;
    }
    
    public static Usuario criar(String nome, String username, String senha){
        if(nome == null || nome.isBlank() || username == null || username.isBlank() || senha == null || senha.isBlank()){
            throw new IllegalArgumentException("Nome, username e senha são obrigatórios.");
        }
        if(consultar(username) != null){
            throw new IllegalArgumentException("O username " + username + " já está cadastrado.");
        }
        Usuario novoUsuario = new Usuario(nome, username, senha);
        usuariosCadastrados.add(novoUsuario);
        return novoUsuario;
    }
    public static Usuario consultar(String username){
        for(Usuario u : usuariosCadastrados) {
            if(u.username.equalsIgnoreCase(username)){
                return u;
            }
        }
        return null;
    }
    public static boolean autenticar(String username, String senha){
    Usuario u = consultar(username);
    if(u == null || senha == null){
        return false;
    }
    String senhaHash = gerarHash(senha);
    return u.senha.equals(senhaHash);
    }
    public static List<Usuario> listar(){
        return new ArrayList<>(usuariosCadastrados);
    }
    //Outros tipos de métodos

    public void editar(String nome, String username, String senha){
        verificarContaAtiva();
        if(username != null && !username.equalsIgnoreCase(this.username) && consultar(username) != null) {
            throw new IllegalArgumentException("O username " + username + " já está cadastrado.");
        }
        this.nome = nome;
        this.username = username;
        if(senha != null && !senha.isBlank()){
            this.senha = gerarHash(senha);
        }
    }
    public void desativar(){
        verificarContaAtiva();
        this.ativo = false;

        List<Grupo> gruposDoUsuario = Grupo.listarPorUsuario(this);
        for(Grupo grupo : gruposDoUsuario) {
            Participacao.excluirParticipacao(this, grupo);
        }
    }
    public void reativar(){
        this.ativo = true;
    }
    private void verificarContaAtiva(){
        if(!this.ativo){
            throw new IllegalStateException("A conta está inativa. A única ação permitida é reativar a conta");
        }
    }
    private static String gerarHash(String texto){
    try{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for(byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Erro ao gerar hash da senha.", e);
    }
    }
    public String getNome(){
        return nome;
    }
    public String getUsername(){
        return username;
    }
    public boolean ehAtivo(){
        return ativo;
    }
    @Override 
    public String toString(){
        return "Usuario{nome='" + nome + "', username='" + username + "', ativo=" + ativo + "}";
    }
}
