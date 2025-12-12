// Pacote para classes de configuração
package __SpringBoot2.__star_Spring_io.configurer;

// Importações para manipulação de lista
import java.util.List;

// Importações Spring para configuração MVC
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration: Marca classe como fonte de configuração Spring
// Implementa WebMvcConfigurer para customizar configurações MVC
@Configuration
public class DavdojoWebMvcConfigurer implements WebMvcConfigurer {
    
    // Sobrescreve método para adicionar resolvedores de argumentos
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Cria resolvedor para parâmetros Pageable (paginação Spring Data)
        PageableHandlerMethodArgumentResolver pageHandler = new PageableHandlerMethodArgumentResolver();
        
        // Configura página padrão quando não for especificada
        // PageRequest.of(0, 5) = página 0 (primeira) com 5 itens por página
        pageHandler.setFallbackPageable(PageRequest.of(0, 5));
        
        // Define tamanho máximo de página (evita carregar muitos dados)
        pageHandler.setMaxPageSize(50);
        
        // Adiciona o resolvedor à lista de resolvedores do Spring
        resolvers.add(pageHandler);
    }
}

// FUNCIONALIDADE:
// Configura automaticamente a paginação (Pageable) nos controllers

// COMO FUNCIONA:
// Quando um método de controller tem parâmetro Pageable:
// @GetMapping
// public List<Anime> list(Pageable pageable) { ... }
// 
// O Spring automaticamente converte parâmetros de URL:
// Ex: /animes?page=0&size=10&sort=name,asc
// pageable = página 0, 10 itens, ordenado por nome ascendente

// CONFIGURAÇÕES DEFINIDAS:
// 1. Padrão: página 0 com 5 itens (quando não especificado)
// 2. Máximo: 50 itens por página (limita abusos)

// EXEMPLOS DE USO:

// 1. Sem parâmetros (usa padrão):
//    GET /animes → page=0, size=5, sem ordenação

// 2. Com parâmetros customizados:
//    GET /animes?page=2&size=20 → página 2, 20 itens

// 3. Com ordenação:
//    GET /animes?sort=name,desc&page=1 → ordena por nome desc, página 1

// 4. Tentativa de exceder limite:
//    GET /animes?size=100 → será limitado a 50 itens (maxPageSize)

// BENEFÍCIOS:
// - Padronização automática de paginação
// - Evita queries com muitos dados (maxPageSize)
// - Interface consistente para frontend
// - Integração com Spring Data JPA

// OBSERVAÇÃO IMPORTANTE:
// Esta configuração só funciona com parâmetro Pageable
// Para PageRequest customizado, usar diretamente no método

// ALTERNATIVA SEM CONFIGURAÇÃO (no controller):
/*
@GetMapping
public List<Anime> list(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "5") int size) {
    // Lógica manual de paginação
}
*/
// A configuração torna isso automático e padronizado