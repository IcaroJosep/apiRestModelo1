// Pacote da controller - organiza classes por funcionalidade
// Convenção: .controller para classes que lidam com requisições HTTP
package __SpringBoot2.__star_Spring_io.controller;

// Importa classes para manipulação de datas
import java.time.LocalDateTime;

// Spring Framework imports - para paginação e respostas HTTP
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import __SpringBoot2.__star_Spring_io.requests.AnimePutRequestBody;
import __SpringBoot2.__star_Spring_io.requests.AnimePostRequestBody;
import __SpringBoot2.__star_Spring_io.requests.AnimeResponse;
import __SpringBoot2.__star_Spring_io.services.AnimeServices;
import __SpringBoot2.__star_Spring_io.util.DateUtil;
// Jakarta Validation - para validação de dados de entrada
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
// Lombok - gera código automaticamente (construtores, logs)
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

// Anotações da classe:
// @RestController = @Controller + @ResponseBody
// Marca a classe como controller REST (retorna JSON/XML)
@RestController
// @RequestMapping define o prefixo URL para todos os endpoints desta controller
// Todas as URLs começarão com /animes
@RequestMapping("animes")
// @Log4j2 cria logger automático para a classe (log.info(), log.error())
@Log4j2
// @RequiredArgsConstructor gera construtor com argumentos obrigatórios (final fields)
// Injeta automaticamente as dependências (DateUtil, AnimeServices)
@RequiredArgsConstructor
// @Validated habilita validação nos parâmetros dos métodos
@Validated
public class AnimeComtroller {

    // Dependências injetadas automaticamente pelo Spring
    // final = obrigatório, Spring injeta via construtor gerado pelo Lombok
    private final DateUtil dateUtil;          // Utilitário para formatação de datas
    private final AnimeServices animeServices; // Camada de serviço (regras de negócio)

    // ENDPOINT 1: Listar todos os animes (com paginação)
    // GET /animes
    @GetMapping
    public ResponseEntity<Page<AnimeResponse>> list(Pageable pageable) {
        // Loga a data/hora da requisição (para monitoramento)
        log.info(dateUtil.formatLocalDataTimeToDatabaseStyle(LocalDateTime.now()));
        
        // Chama serviço para obter lista paginada de animes
        Page<AnimeResponse> listAnime = animeServices.listAll(pageable);
        
        // Retorna HTTP 200 OK com a lista no corpo da resposta
        return ResponseEntity.ok(listAnime);
    }

    // ENDPOINT 2: Buscar animes por nome (com validações)
    // GET /animes/findByName?name=Naruto&comtem=false
    @GetMapping(path = "findByName")
    public ResponseEntity<Page<AnimeResponse>> list(
            // Pageable: Spring fornece automaticamente paginação via parâmetros:
            // ?page=0&size=10&sort=nome,asc
            Pageable pageable,
            // @RequestParam: parâmetro da URL (?name=valor)
            // Validações aplicadas:
            @RequestParam 
            @NotBlank(message = "Nome não pode ser vazio")              // Não pode ser null, vazio ou só espaços
            @Size(min = 1, max = 50, message = "Nome deve ter entre 1 e 50 caracteres")  // Tamanho do texto
            @Pattern(regexp = "^[a-zA-Z0-9áàâãéèêíïóôõöúçñÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ\\s\\-._]*$", 
                     message = "Caracteres inválidos no nome")  // Caracteres permitidos (regex)
            String name,
            // Parâmetro opcional com valor padrão false
            @RequestParam(defaultValue = "false") 
            boolean comtem  // Flag para tipo de busca (contém/exato)
    ) {
        // Chama serviço de busca com os parâmetros
        Page<AnimeResponse> listAnime = animeServices.findByName(pageable, name, comtem);
        
        return ResponseEntity.ok(listAnime);
    }

    // ENDPOINT 3: Criar novo anime
    // POST /animes
    @PostMapping
    public ResponseEntity<AnimeResponse> save(
            // @RequestBody: dados vem no corpo da requisição (JSON)
            // @Valid: valida o objeto usando anotações da classe AnimePostRequestBody
            @RequestBody @Valid AnimePostRequestBody animePostRequestBody) {
        
        // Chama serviço para salvar o anime
        AnimeResponse animeSalvo = animeServices.save(animePostRequestBody);
        
        // Retorna HTTP 201 CREATED com o anime salvo no corpo
        // Diferente do HTTP 200 OK, 201 indica criação bem-sucedida
        return new ResponseEntity<>(animeSalvo, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<AnimeResponse> delete(@PathVariable Long id){
    	return ResponseEntity.ok(animeServices.deleteById(id));
    }
    
    @PutMapping
    public ResponseEntity<AnimeResponse> update(@RequestBody @Valid AnimePutRequestBody animePutRequestBody){
    	
    	return ResponseEntity.ok(animeServices.updateByName(animePutRequestBody.getId(),animePutRequestBody.getName()));
    }
    
    
}

// RESUMO DOS ENDPOINTS:
// GET    /animes              → Lista todos (paginação)
// GET    /animes/findByName   → Busca por nome (com validações)
// POST   /animes              → Cria novo anime

// FLUXO TÍPICO DE UMA REQUISIÇÃO:
// 1. Cliente faz requisição HTTP para /animes
// 2. Spring rota para método correto (@GetMapping, @PostMapping)
// 3. Valida parâmetros (@Valid, @NotBlank, etc.)
// 4. Chama camada de serviço (AnimeServices)
// 5. Service chama repository (não mostrado aqui)
// 6. Repository acessa banco de dados
// 7. Retorna resposta HTTP com dados ou status de erro

// VALIDAÇÕES IMPORTANTES:
// - @Validated na classe: habilita validação nos parâmetros dos métodos
// - @Valid no método: valida o objeto do corpo da requisição
// - Jakarta Validation: @NotBlank, @Size, @Pattern para validação de campos

// PAGINAÇÃO AUTOMÁTICA:
// Pageable recebe automaticamente: ?page=0&size=10&sort=campo,asc
// Spring cria objeto Pageable com essas informações
// Retorna Page<Anime> com: conteúdo, total de páginas, elementos totais

// CÓDIGOS HTTP USADOS:
// 200 OK - Sucesso em GET
// 201 CREATED - Sucesso em POST (recurso criado)
// 400 BAD REQUEST - Validação falhou (Spring retorna automaticamente)
// 404 NOT FOUND - Recurso não existe (se implementado)
// 500 INTERNAL SERVER ERROR - Erro no servidor