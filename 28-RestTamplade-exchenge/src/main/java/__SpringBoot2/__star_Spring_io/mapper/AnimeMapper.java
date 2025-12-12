// Pacote onde o mapper está localizado
package __SpringBoot2.__star_Spring_io.mapper;

// Importações do MapStruct - framework de mapeamento objeto-objeto
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// Importações das classes que serão convertidas
import __SpringBoot2.__star_Spring_io.dominio.Anime;
import __SpringBoot2.__star_Spring_io.requests.AnimePostRequestBody;
import __SpringBoot2.__star_Spring_io.requests.AnimePutRequestBody;

// Anotação @Mapper do MapStruct
// componentModel="spring" torna esta interface um bean gerenciado pelo Spring
@Mapper(componentModel = "spring")
public interface AnimeMapper {
    
    // Converte AnimePostRequestBody para entidade Anime
    Anime toAnime(AnimePostRequestBody animePostRequestBody);
    
    // Converte AnimePutRequestBody para entidade Anime
    Anime toAnime(AnimePutRequestBody animePutRequestBody);
}

// ALTERNATIVA SEM SPRING (instância manual):
/*
@Mapper // Anotação básica sem integração com Spring
public interface AnimeMapper {
    
    // Instância única acessível globalmente
    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);
    
    // Mesmos métodos de conversão
    Anime toAnime(AnimePostRequestBody animePostRequestBody);
    Anime toAnime(AnimePutRequestBody animePutRequestBody);
}
*/

// FUNCIONAMENTO DO MAPSTRUCT:
// 1. Em tempo de compilação, gera implementação automática desta interface
// 2. Código gerado converte campos com mesmo nome automaticamente
// 3. Com componentModel="spring", cria um @Component que pode ser injetado com @Autowired

// VANTAGENS:
// - Reduz código boilerplate de conversão
// - Seguro em tempo de compilação
// - Alta performance (não usa reflection)

// COMO USAR NO SPRING:
// 1. Injete em qualquer classe: @Autowired private AnimeMapper animeMapper;
// 2. Use os métodos: Anime anime = animeMapper.toAnime(animePostRequestBody);

// FLUXO TÍPICO:
// Controller recebe DTO (AnimePostRequestBody) → Mapper converte para Entidade (Anime) → Service salva no banco