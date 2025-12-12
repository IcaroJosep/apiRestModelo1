// Pacote para interfaces de repositório
package __SpringBoot2.__star_Spring_io.repository;

// Importações Spring Data para paginação e JPA
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// Importação da entidade do domínio
import __SpringBoot2.__star_Spring_io.dominio.Anime;

// Interface de repositório para a entidade Anime
// Extende JpaRepository que fornece operações CRUD básicas automaticamente
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    // Herda automaticamente: save(), findById(), findAll(), delete(), count(), etc.
    
    // ========== BUSCA PAGINADA POR NOME (CONTÉM) ==========
    // Retorna página de animes cujo nome CONTÉM a string fornecida
    // Spring Data implementa automaticamente baseado no nome do método
    Page<Anime> findByNameContaining(String name, Pageable pageable);
    
    // ========== BUSCA PAGINADA POR NOME (EXATO) ==========
    // Retorna página de animes cujo nome é EXATAMENTE igual ao fornecido
    Page<Anime> findByName(String name, Pageable pageable);
}

// CONCEITO: REPOSITORY PATTERN
// Interface que abstrai acesso a dados, separando lógica de persistência

// O QUE JpaRepository JÁ FORNECE AUTOMATICAMENTE:
// 1. save(Anime anime) - Salva ou atualiza
// 2. findById(Long id) - Busca por ID
// 3. findAll() - Busca todos
// 4. findAll(Pageable pageable) - Busca paginada
// 5. deleteById(Long id) - Remove por ID
// 6. count() - Conta total
// 7. existsById(Long id) - Verifica existência

// QUERY METHODS (MÉTODOS DE CONSULTA):
// Spring Data implementa consultas automaticamente baseado no nome do método:
// 
// Padrão: find + By + Atributo + Operação + Parâmetros
// Exemplos que funcionariam:
// - findByNameIgnoreCase(String name)
// - findByNameStartingWith(String prefix)
// - findByNameEndingWith(String suffix)
// - findByNameAndId(String name, Long id)
// - findByOrderByNameAsc()

// PARÂMETROS ESPECIAIS:
// Pageable: Controla paginação (página, tamanho, ordenação)
// Sort: Apenas ordenação sem paginação

// EXEMPLO DE USO NO SERVICE:
// Page<Anime> pagina = animeRepository.findByNameContaining("naruto", pageable);
// 
// SQL gerado automaticamente (aproximado):
// SELECT * FROM anime WHERE name LIKE '%naruto%' LIMIT ? OFFSET ?

// PERFORMANCE:
// - Paginação evita carregar todos os dados de uma vez
// - Consultas otimizadas pelo JPA/Hibernate
// - Cache de segundo nível opcional

// CONFIGURAÇÃO NECESSÁRIA NO APPLICATION.PROPERTIES:
// spring.datasource.url=jdbc:mysql://localhost:3306/db_anime
// spring.datasource.username=usuario
// spring.datasource.password=senha
// spring.jpa.hibernate.ddl-auto=update
// spring.jpa.show-sql=true (para desenvolvimento)

// MÉTODOS ADICIONAIS POSSÍVEIS:
// 1. Busca por data: findByReleaseDateAfter(Date date)
// 2. Busca com múltiplos critérios: findByNameAndActiveTrue(String name)
// 3. Consultas personalizadas com @Query:
//    @Query("SELECT a FROM Anime a WHERE a.name LIKE %:name% AND a.active = true")
//    Page<Anime> findActiveByName(@Param("name") String name, Pageable pageable);

// BENEFÍCIOS DESTA ABORDAGEM:
// - Código limpo (sem implementação manual)
// - Type-safe (erros em tempo de compilação)
// - Fácil manutenção
// - Padronização

// CENÁRIOS DE USO:
// 1. Listagem com filtro: findByNameContaining + Pageable
// 2. Busca exata: findByName
// 3. CRUD completo: métodos herdados do JpaRepository