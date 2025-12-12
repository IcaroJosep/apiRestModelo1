// Pacote de domínio - contém as entidades principais do sistema
// Representam tabelas do banco de dados e objetos de negócio
package __SpringBoot2.__star_Spring_io.dominio;

// Anotações JPA (Jakarta Persistence API) para mapeamento objeto-relacional
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Anotações Lombok para gerar código automaticamente
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CLASSE Anime
 * 
 * Esta é uma ENTIDADE JPA que representa a tabela "anime" no banco de dados.
 * Mapeia um objeto Java para um registro na tabela.
 * 
 * Convenção: Nome da classe no singular (Anime), tabela no plural (animes - gerado automaticamente)
 */
@Data  // Lombok: Gera getters, setters, toString, equals, hashCode
@AllArgsConstructor  // Lombok: Gera construtor com todos os campos
@NoArgsConstructor   // Lombok: Gera construtor vazio (obrigatório para JPA)
@Entity  // JPA: Marca esta classe como uma entidade persistente
@Builder // Lombok: Implementa o padrão Builder para criar objetos de forma fluente
public class Anime {
    
    /**
     * CAMPO: id
     * 
     * Identificador único do anime (chave primária)
     * 
     * @Id - JPA: Marca este campo como chave primária
     * @GeneratedValue - JPA: Como o valor é gerado
     *   strategy = GenerationType.IDENTITY: Banco gera automaticamente (auto_increment no MySQL)
     * 
     * Outras estratégias:
     *   - SEQUENCE: Usa sequência do banco (Oracle, PostgreSQL)
     *   - TABLE: Usa tabela especial para controle
     *   - AUTO: Deixa o JPA escolher
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Usar Long em vez de long para permitir null (não persistido ainda)
    
    /**
     * CAMPO: name
     * 
     * Nome do anime
     * 
     * @Column - JPA: Configurações da coluna no banco
     *   nullable = false: Campo obrigatório (NOT NULL no banco)
     *   length = 100: Tamanho máximo da coluna VARCHAR(100)
     * 
     * Outras opções úteis:
     *   - unique = true: Garante valores únicos
     *   - name = "nome_anime": Nome personalizado da coluna
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * O QUE LOMBOK GERA AUTOMATICAMENTE:
     * 
     * 1. @Data gera:
     *    - public Long getId() { return id; }
     *    - public void setId(Long id) { this.id = id; }
     *    - public String getName() { return name; }
     *    - public void setName(String name) { this.name = name; }
     *    - equals(), hashCode(), toString()
     * 
     * 2. @AllArgsConstructor gera:
     *    public Anime(Long id, String name) { ... }
     * 
     * 3. @NoArgsConstructor gera:
     *    public Anime() { }
     * 
     * 4. @Builder gera:
     *    Anime anime = Anime.builder()
     *        .id(1L)
     *        .name("Naruto")
     *        .build();
     */
}

/**
 * MAPEAMENTO PARA TABELA DO BANCO (exemplo MySQL):
 * 
 * CREATE TABLE anime (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     name VARCHAR(100) NOT NULL
 * );
 */

/**
 * COMO USAR ESTA ENTIDADE:
 * 
 * 1. CRIANDO OBJETO (construtor normal):
 *    Anime anime = new Anime();
 *    anime.setName("Naruto");
 * 
 * 2. CRIANDO COM BUILDER (padrão mais legível):
 *    Anime anime = Anime.builder()
 *        .name("Naruto")
 *        .build();
 *    // ID é gerado pelo banco, não precisa setar
 * 
 * 3. USANDO CONSTRUTOR COMPLETO:
 *    Anime anime = new Anime(1L, "Naruto");
 */

/**
 * BOAS PRÁTICAS PARA ENTIDADES JPA:
 * 
 * 1. Use classes wrapper (Long, Integer) em vez de primitivos (long, int)
 *    - Permite null quando objeto não está persistido
 * 
 * 2. Mantenha construtor padrão (vazio) - @NoArgsConstructor faz isso
 *    - Obrigatório para JPA/Hibernate
 * 
 * 3. Evite lógica de negócio complexa nas entidades
 *    - Entidades devem ser simples (anêmicas)
 *    - Lógica vai para Services
 * 
 * 4. Considere validações:
 *    - @NotNull, @Size, @Pattern (Jakarta Validation)
 *    - Pode ser combinado com @Column
 */

/**
 * MELHORIAS POSSÍVEIS:
 * 
 * 1. ADICIONAR VALIDAÇÕES:
 *    @Column(nullable = false, length = 100)
 *    @NotBlank(message = "Nome não pode ser vazio")
 *    @Size(min = 1, max = 50, message = "Nome deve ter entre 1 e 50 caracteres")
 *    private String name;
 * 
 * 2. ADICIONAR MAIS CAMPOS:
 *    private String genero;
 *    private Integer episodios;
 *    private LocalDate dataLancamento;
 * 
 * 3. ADICIONAR RELACIONAMENTOS:
 *    @OneToMany(mappedBy = "anime")
 *    private List<Episodio> episodios;
 * 
 * 4. ADICIONAR AUDITORIA:
 *    @CreatedDate
 *    private LocalDateTime dataCriacao;
 *    
 *    @LastModifiedDate
 *    private LocalDateTime dataAtualizacao;
 */

/**
 * CICLO DE VIDA DA ENTIDADE:
 * 
 * 1. TRANSIENT: Objeto criado mas não associado à sessão do Hibernate
 *    Anime anime = new Anime();
 * 
 * 2. PERSISTENT: Objeto associado à sessão (salvo ou carregado)
 *    animeRepository.save(anime);
 * 
 * 3. DETACHED: Sessão fechada, mas objeto ainda existe
 * 
 * 4. REMOVED: Marcado para exclusão do banco
 *    animeRepository.delete(anime);
 */

/**
 * IMPORTANTE PARA TESTES:
 * 
 * 1. Para testes unitários: Use objetos normalmente
 * 2. Para testes de integração: @DataJpaTest
 * 3. Para mocks: Mockito pode mockar repositories que retornam Anime
 */