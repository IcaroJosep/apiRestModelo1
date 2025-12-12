// Pacote de segurança - classes para validação e proteção
package __SpringBoot2.__star_Spring_io.seguranca;

// Imports para listas e utilitários
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Spring Data imports para paginação e ordenação
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * CLASSE PageableValidation
 * 
 * Propósito: Validar e sanitizar parâmetros de paginação recebidos do cliente
 * Problema resolvido: Clientes podem enviar parâmetros maliciosos como:
 * - pageSize=1000000 (sobrecarrega banco de dados)
 * - sort=;DROP TABLE (ataque de injeção)
 * - page=-1 (página inválida)
 */
public class PageableValidation {

    // CONSTANTES DE CONFIGURAÇÃO (ajustáveis conforme necessidade)
    
    // Máximo de itens por página (previne sobrecarga)
    private static final int MAX_PAGE_SIZE = 50;
    
    // Tamanho padrão quando não especificado ou inválido
    private static final int DEFAULT_PAGE_SIZE = 5;
    
    // Página padrão (começa em 0, convenção Spring)
    private static final int DEFAULT_PAGE_NUMBER = 0;
    
    // Campos que podem ser usados para ordenação
    // IMPORTANTE: Ajustar conforme campos da entidade Anime
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("id", "name");

    /**
     * MÉTODO PRINCIPAL: Valida e sanitiza completamente um Pageable
     * 
     * @param pageable - Objeto Pageable recebido do cliente (potencialmente inseguro)
     * @return Pageable seguro para uso no banco de dados
     * 
     * Processa 3 aspectos:
     * 1. Tamanho da página (pageSize)
     * 2. Número da página (pageNumber)
     * 3. Ordenação (sort)
     */
    public static Pageable validateAndSanitize(Pageable pageable) {
        // 1. Valida tamanho da página
        int safeSize = validatePageSize(pageable.getPageSize());
        
        // 2. Valida número da página
        int safePage = validatePageNumber(pageable.getPageNumber());
        
        // 3. Valida e sanitiza ordenação (parte mais crítica)
        Sort safeSort = validateAndSanitizeSort(pageable.getSort());
        
        // 4. Cria novo Pageable seguro
        return PageRequest.of(safePage, safeSize, safeSort);
    }

    /**
     * Valida e ajusta o tamanho da página
     * 
     * @param requestedSize - Tamanho solicitado pelo cliente
     * @return Tamanho seguro (entre 1 e MAX_PAGE_SIZE)
     */
    private static int validatePageSize(int requestedSize) {
        // Se for 0 ou negativo, usa valor padrão
        if (requestedSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        // Garante que não ultrapasse o máximo permitido
        return Math.min(requestedSize, MAX_PAGE_SIZE);
    }

    /**
     * Valida e ajusta o número da página
     * 
     * @param requestedPage - Página solicitada pelo cliente
     * @return Número seguro (não negativo)
     */
    private static int validatePageNumber(int requestedPage) {
        // Garante que página seja 0 ou maior
        return Math.max(requestedPage, DEFAULT_PAGE_NUMBER);
    }

    /**
     * Valida e sanitiza parâmetros de ordenação (CRÍTICO PARA SEGURANÇA)
     * 
     * @param requestedSort - Objeto Sort do cliente
     * @return Sort seguro para usar em queries
     */
    private static Sort validateAndSanitizeSort(Sort requestedSort) {
        // Se não houver ordenação ou estiver vazia, usa padrão
        if (requestedSort == null || requestedSort.isEmpty()) {
            return Sort.by("id").ascending(); // Ordenação padrão segura
        }

        // Processa cada critério de ordenação
        List<Order> safeOrders = requestedSort.stream()
                // Filtra apenas campos permitidos
                .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
                // Sanitiza o nome do campo (remove HTML/JS)
                .map(order -> new Order(
                        order.getDirection(),                    // Mantém direção (ASC/DESC)
                        Sanatizador.saniString(order.getProperty()) // Sanitiza nome do campo
                ))
                .collect(Collectors.toList());

        // Se nenhum campo for válido após filtragem, usa padrão
        if (safeOrders.isEmpty()) {
            return Sort.by("id").ascending();
        }

        // Retorna ordenação sanitizada
        return Sort.by(safeOrders);
    }

    /**
     * VALIDAÇÃO RÁPIDA (menos rigorosa)
     * Apenas valida tamanho da página, mantém o resto original
     * 
     * @param pageable - Pageable a ser validado
     * @return Pageable com tamanho ajustado
     */
    public static Pageable quickValidate(Pageable pageable) {
        int safeSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        if (safeSize <= 0) {
            safeSize = DEFAULT_PAGE_SIZE;
        }

        // Mantém ordenação original (menos seguro)
        return PageRequest.of(
            Math.max(pageable.getPageNumber(), 0), // Página não negativa
            safeSize,                              // Tamanho limitado
            pageable.getSort()                     // Ordenação original (CUIDADO!)
        );
    }

    /**
     * Verifica se um campo pode ser usado para ordenação
     * 
     * @param fieldName - Nome do campo a verificar
     * @return true se o campo é permitido
     */
    public static boolean isSortFieldAllowed(String fieldName) {
        return ALLOWED_SORT_FIELDS.contains(fieldName);
    }

    /**
     * Obtém lista de campos permitidos para ordenação
     */
    public static List<String> getAllowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }

    /**
     * Obtém tamanho máximo de página
     */
    public static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }
}

/**
 * COMO USAR NA PRÁTICA:
 * 
 * NA CONTROLLER (recomendado):
 * @GetMapping
 * public ResponseEntity list(Pageable pageable) {
 *     Pageable safePageable = PageableValidation.validateAndSanitize(pageable);
 *     return service.listAll(safePageable);
 * }
 * 
 * OU NO SERVICE:
 * public Page<Anime> listAll(Pageable pageable) {
 *     Pageable safePageable = PageableValidation.quickValidate(pageable);
 *     return repository.findAll(safePageable);
 * }
 */

/**
 * RISCOS EVITADOS:
 * 
 * 1. DOS/OVERCARGA:
 *    Cliente envia: ?size=1000000
 *    Resultado: size=50 (máximo permitido)
 * 
 * 2. INJEÇÃO SQL INDIRETA:
 *    Cliente envia: ?sort=name;DROP TABLE anime
 *    Resultado: sort removido (campo não permitido)
 * 
 * 3. PÁGINA INVÁLIDA:
 *    Cliente envia: ?page=-10
 *    Resultado: page=0 (página padrão)
 * 
 * 4. CAMPOS INEXISTENTES:
 *    Cliente envia: ?sort=senha,desc
 *    Resultado: sort=id,asc (padrão seguro)
 */

/**
 * CONFIGURAÇÕES IMPORTANTES:
 * 
 * 1. MAX_PAGE_SIZE: Ajuste baseado em:
 *    - Performance do banco
 *    - Uso típico da aplicação
 *    - Limites da infraestrutura
 * 
 * 2. ALLOWED_SORT_FIELDS: DEVE refletir:
 *    - Campos existentes na entidade
 *    - Campos com índice no banco
 *    - Campos que fazem sentido ordenar
 * 
 * 3. DEFAULT_PAGE_SIZE: Balance entre:
 *    - Número de requisições (muito pequeno)
 *    - Tamanho da resposta (muito grande)
 */

/**
 * BOAS PRÁTICAS ADICIONAIS:
 * 
 * 1. LOG DE TENTATIVAS INVÁLIDAS:
 *    if(requestedSize > MAX_PAGE_SIZE) {
 *        log.warn("Tentativa de pageSize excessivo: " + requestedSize);
 *    }
 * 
 * 2. VALIDAÇÃO POR PERFIL:
 *    - Desenvolvimento: limites mais altos
 *    - Produção: limites restritivos
 * 
 * 3. CACHE DE RESULTADOS:
 *    Para páginas comuns (primeira página, tamanho padrão)
 * 
 * 4. DOCUMENTAÇÃO DA API:
 *    Informar aos clientes os limites permitidos
 */