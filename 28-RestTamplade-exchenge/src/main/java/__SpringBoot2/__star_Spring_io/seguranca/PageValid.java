// Pacote de segurança - validação de páginas
package __SpringBoot2.__star_Spring_io.seguranca;

// Imports para manipulação de listas e streams
import java.util.List;
import java.util.stream.Collectors;

// Spring Data para paginação
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

// Classes do projeto
import __SpringBoot2.__star_Spring_io.dominio.Anime;
import __SpringBoot2.__star_Spring_io.exception.BedRequestException;

/**
 * CLASSE PageValid
 * 
 * Propósito: Validar e sanitizar uma PÁGINA já carregada do banco de dados
 * Diferente de PageableValidation que valida PARÂMETROS de entrada,
 * esta classe valida o RESULTADO da consulta.
 * 
 * Uso típico: Após buscar dados no banco, antes de retornar ao cliente
 */
public class PageValid {
    
    // Tamanho máximo permitido para uma página (consistente com PageableValidation)
    private static final int MAX_PAGE_SIZE = 50;
    
    /**
     * MÉTODO PRINCIPAL: Valida e sanitiza uma página de Animes
     * 
     * @param page - Página retornada pelo repositório/banco de dados
     * @return Página validada e sanitizada
     * @throws BedRequestException - Se a página for inválida
     * 
     * Fluxo:
     * 1. Verifica se página é null
     * 2. Valida tamanho da página
     * 3. Valida número da página
     * 4. Sanitiza conteúdo (se houver)
     * 5. Retorna nova página segura
     */
    public static Page<Anime> ValidaSanitizaPageAnime(Page<Anime> page) {
        
        // 1. VALIDAÇÃO CRÍTICA: página não pode ser null
        if (page == null) {
            throw new BedRequestException("page null!!");
        }
        
        // 2. VALIDA tamanho da página (pageSize)
        int pageSize = page.getSize();
        if (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) {
            throw new BedRequestException(
                String.format(
                    "Tamanho da página inválido: %d. Deve estar entre 1 e %d", 
                    pageSize, 
                    MAX_PAGE_SIZE
                )
            );
        }
        
        // 3. VALIDA número da página (não pode ser negativo)
        if (page.getNumber() < 0) {
            throw new BedRequestException(
                String.format(
                    "Número da página inválido: %d. Não pode ser negativo", 
                    page.getNumber()
                )
            );
        }
        
        // 4. SE PÁGINA VAZIA: retorna sem processar conteúdo
        if (!page.hasContent()) {
            return page; // Página vazia já é segura
        }
        
        // 5. SANITIZAÇÃO DO CONTEÚDO (proteção XSS na saída)
        // Cria NOVA lista com Animes sanitizados
        List<Anime> sanitizedContent = page.getContent().stream()
            .map(Sanatizador::saniAnime) // Aplica sanitização em cada Anime
            .collect(Collectors.toList());
        
        // 6. CRIA NOVA PÁGINA com conteúdo sanitizado
        // PageImpl é implementação concreta de Page
        Page<Anime> saniPage = new PageImpl<>(
            sanitizedContent,          // Conteúdo sanitizado
            page.getPageable(),       // Configuração original (página, tamanho, ordenação)
            page.getTotalElements()   // Total de registros (importante para paginação)
        );
        
        return saniPage;
    }
}

/**
 * DIFERENÇA ENTRE PageValid E PageableValidation:
 * 
 * PageableValidation (anterior):
 * - VALIDA PARÂMETROS DE ENTRADA (?page=0&size=10)
 * - Usado ANTES da consulta ao banco
 * - Foco: prevenir ataques na construção da query
 * 
 * PageValid (esta classe):
 * - VALIDA RESULTADO DA CONSULTA
 * - Usado DEPOIS de buscar no banco
 * - Foco: garantir integridade dos dados retornados
 */

/**
 * POR QUE SANITIZAR NA SAÍDA (output)?
 * 
 * Defesa em profundidade (defense in depth):
 * 1. Entrada sanitizada (no controller/service)
 * 2. Dados seguros no banco
 * 3. Saída sanitizada (última linha de defesa)
 * 
 * Cenário: Dado malicioso entra no banco (bug anterior)
 * Esta classe garante que mesmo assim não será retornado
 */

/**
 * EXEMPLO DE USO NO SERVICE:
 * 
 * public Page<Anime> listAll(Pageable pageable) {
 *     // 1. Valida parâmetros de entrada
 *     Pageable safePageable = PageableValidation.validateAndSanitize(pageable);
 *     
 *     // 2. Busca no banco
 *     Page<Anime> pageFromDB = repository.findAll(safePageable);
 *     
 *     // 3. Valida e sanitiza resultado
 *     return PageValid.ValidaSanitizaPageAnime(pageFromDB);
 * }
 */

/**
 * VALIDAÇÕES REALIZADAS:
 * 
 * 1. NULL CHECK: page != null
 * 2. SIZE VALIDATION: 1 <= size <= 50
 * 3. PAGE NUMBER: number >= 0
 * 4. CONTENT CHECK: se vazio, retorna rápido
 * 5. CONTENT SANITIZATION: remove HTML/JS de cada Anime
 */

/**
 * OBSERVAÇÕES IMPORTANTES:
 * 
 * 1. IMUTABILIDADE: Cria NOVA página, não modifica a original
 *    - Mantém dados originais intactos
 *    - Evita efeitos colaterais
 * 
 * 2. PERFORMANCE: Stream + map pode ser custoso para páginas grandes
 *    - MAX_PAGE_SIZE limita impacto
 *    - Considerar cache para páginas comuns
 * 
 * 3. EXCEÇÕES: Usa BedRequestException (HTTP 400)
 *    - Cliente recebe erro claro
 *    - Logs facilitam debugging
 * 
 * 4. CONSISTÊNCIA: MAX_PAGE_SIZE deve ser igual em todas as classes
 *    - Esta classe: 50
 *    - PageableValidation: 50
 *    - application.properties: spring.data.web.pageable.max-page-size=50
 */

/**
 * MELHORIAS POSSÍVEIS:
 * 
 * 1. GENÉRICOS: public static <T> Page<T> validatePage(Page<T> page)
 *    - Funcionaria para qualquer entidade
 *    - Necessitaria função de sanitização genérica
 * 
 * 2. CONFIGURAÇÃO EXTERNA:
 *    @Value("${page.validation.max-size:50}")
 *    private int maxPageSize;
 * 
 * 3. LOGS DETALHADOS:
 *    log.warn("Página inválida recebida: size={}, number={}", size, number);
 * 
 * 4. METRICS:
 *    monitorar quantidade de validações falhas
 */

/**
 * ERROS COMUNS EVITADOS:
 * 
 * 1. Page com size=0: causaria divisão por zero em cálculos
 * 2. Page com número negativo: lógica de paginação quebrada
 * 3. Conteúdo não sanitizado: XSS na interface do usuário
 * 4. Page null: NullPointerException no controller
 */