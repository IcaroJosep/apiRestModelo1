// Pacote para classes de serviço (camada de negócio)
package __SpringBoot2.__star_Spring_io.services;

// Importações Spring Data para paginação
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// Importações de domínio, exceções, mappers e repositórios
import __SpringBoot2.__star_Spring_io.dominio.Anime;
import __SpringBoot2.__star_Spring_io.exception.BedRequestException;
import __SpringBoot2.__star_Spring_io.mapper.AnimeMapper;
import __SpringBoot2.__star_Spring_io.repository.AnimeRepository;
import __SpringBoot2.__star_Spring_io.requests.AnimePostRequestBody;
import __SpringBoot2.__star_Spring_io.seguranca.PageValid;
import __SpringBoot2.__star_Spring_io.seguranca.PageableValidation;
import __SpringBoot2.__star_Spring_io.seguranca.Sanatizador;
// Lombok para injeção de dependências via construtor
import lombok.RequiredArgsConstructor;

// @Service: Marca classe como componente de serviço (gerenciado pelo Spring)
// @RequiredArgsConstructor: Gera construtor com campos final (injeção automática)
@Service
@RequiredArgsConstructor
public class AnimeServices {
    
    // Repositório para operações de banco de dados
    private final AnimeRepository animeRepository;
    
    // Mapper para conversão entre DTO e entidade
    private final AnimeMapper animeMapper;
    
    // ========== LISTA TODOS OS ANIMES (COM PAGINAÇÃO) ==========
    public Page<Anime> listAll(Pageable pageable) {
        // Valida e sanitiza parâmetros de paginação
        Pageable pageableRequest = PageableValidation.validateAndSanitize(pageable);
        
        // Busca todos os animes paginados e sanitiza o resultado
        Page<Anime> pSani = PageValid.ValidaSanitizaPageAnime(
            animeRepository.findAll(pageableRequest)
        );
        
        return pSani;
    }
    
    // ========== BUSCA ANIMES POR NOME ==========
    public Page<Anime> findByName(Pageable pageable, String name, boolean comtem) {
        // Valida e sanitiza paginação
        Pageable pageableRequest = PageableValidation.validateAndSanitize(pageable);
        
        // Sanitiza nome para evitar injeção/ataques
        String nSani = Sanatizador.saniString(name);
        
        // Valida nome sanitizado
        if (nSani == null) {
            throw new BedRequestException("nome invalido");
        }
        
        Page<Anime> pSani;
        
        // Busca por contém ou exato, baseado no parâmetro 'comtem'
        if (comtem) {
            // Busca nomes que CONTÊM o texto (LIKE %texto%)
            pSani = PageValid.ValidaSanitizaPageAnime(
                animeRepository.findByNameContaining(nSani, pageableRequest)
            );
            return pSani;
        }
        
        // Busca nome EXATO (equals)
        pSani = PageValid.ValidaSanitizaPageAnime(
            animeRepository.findByName(nSani, pageableRequest)
        );
        
        return pSani;
    }
    
    // ========== SALVA NOVO ANIME ==========
    public Anime save(AnimePostRequestBody animePostRequestBody) {
        // Cria novo DTO para dados sanitizados
        AnimePostRequestBody dtoSanatizado = new AnimePostRequestBody();
        
        // Sanitiza nome do anime (remove HTML/scripts maliciosos)
        String nameSani = Sanatizador.saniString(animePostRequestBody.getName());
        
        // Valida nome após sanitização
        if (nameSani != null && nameSani.trim().isEmpty()) {
            throw new BedRequestException(
                "Digite um nome válido (apenas tags HTML não são permitidas)"
            );
        }
        
        // Atribui nome sanitizado ao DTO
        dtoSanatizado.setName(nameSani);
        
        // Converte DTO para entidade Anime
        Anime animeInp = animeMapper.toAnime(dtoSanatizado);
        
        // Salva no banco de dados
        Anime animeSalvo = animeRepository.save(animeInp);
        
        // Sanitiza anime retornado do banco (segurança extra)
        return Sanatizador.saniAnime(animeSalvo);
    }
}

// ARQUITETURA EM CAMADAS:
// Controller → Service (esta classe) → Repository → Banco de dados

// RESPONSABILIDADES DESTA CLASSE:
// 1. Lógica de negócio relacionada a animes
// 2. Validação de dados
// 3. Sanitização de entradas/saídas
// 4. Coordenação entre repositório e mapper

// PRINCIPAIS FLUXOS:

// 1. LISTAR TODOS:
//    URL: GET /animes?page=0&size=5
//    → valida paginação → busca no BD → sanitiza → retorna

// 2. BUSCAR POR NOME:
//    URL: GET /animes/search?name=naru&comtem=true
//    → sanitiza nome → busca (contém ou exato) → sanitiza → retorna

// 3. CRIAR NOVO:
//    URL: POST /animes (JSON: {"name": "Naruto"})
//    → sanitiza nome → converte DTO → salva → sanitiza resposta

// SEGURANÇA IMPLEMENTADA:
// - Sanitização de strings (evita XSS)
// - Validação de paginação (evita abusos)
// - Validação de entrada (nomes inválidos)
// - Sanitização de saída (dados retornados)

// OBSERVAÇÕES:
// 1. O método save() poderia validar se anime já existe
// 2. Poderiam ser adicionados métodos update() e delete()
// 3. Poderia incluir logs para auditoria
// 4. Poderia tratar exceções específicas do repositório

// BOAS PRÁTICAS APLICADAS:
// - Separação de responsabilidades
// - Injeção de dependências
// - Sanitização de dados
// - Validação de entrada
// - Paginação para performance