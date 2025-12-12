// Pacote onde a classe de requisição está localizada
package __SpringBoot2.__star_Spring_io.requests;

// Importações para validação de dados
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
// Importação do Lombok para gerar getters, setters, etc automaticamente
import lombok.Data;

// @Data do Lombok - gera automaticamente:
// getters, setters, toString, equals, hashCode
@Data
public class AnimePostRequestBody {
    
    // Campo que representa o nome do anime
    // Anotação @NotEmpty: valida que o campo não pode ser nulo nem string vazia
    // message: mensagem de erro personalizada
    @NotEmpty(message = "o nome de um anime nao pode ser vazio")
    
    // Anotação @Size: valida tamanho da string
    // min=1: mínimo 2 caractere (ajustável para 2 conforme mensagem)
    // max=100: máximo 100 caracteres
    @Size(min = 1, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;
}

// FINALIDADE:
// Classe DTO (Data Transfer Object) usada para receber dados da API
// Padrão comum: separar dados de entrada da entidade do banco

// FLUXO DE USO:
// 1. Cliente envia JSON para API: {"name": "Naruto"}
// 2. Spring converte JSON para esta classe automaticamente
// 3. Spring valida as anotações antes de processar
// 4. Se válido: prossegue para service/repository
// 5. Se inválido: retorna erro 400 com mensagens

// EXEMPLO DE JSON VÁLIDO:
// {
//   "name": "Attack on Titan"
// }

// EXEMPLO DE JSON INVÁLIDO (retorna erro):
// {
//   "name": ""  // String vazia - viola @NotEmpty
// }
// OU
// {
//   "name": null  // Valor nulo - viola @NotEmpty
// }
// OU
// {
//   "name": "ab"  // Muito curto - viola @Size (se min=3)
// }

// INTEGRAÇÃO COM CONTROLLER:
// No método do controller use: @RequestBody @Valid AnimePostRequestBody request
// @Valid ativa as validações das anotações
