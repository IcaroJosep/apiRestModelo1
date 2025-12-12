// Pacote da classe de requisição para atualização
package __SpringBoot2.__star_Spring_io.requests;

// Importações para validação
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
// Lombok para métodos automáticos
import lombok.Data;

// @Data do Lombok: gera getters, setters, toString, equals, hashCode
@Data
public class AnimePutRequestBody {
    
    // Campo ID obrigatório para operações de UPDATE
    // @NotNull: valida que o ID não pode ser nulo
    @NotNull
    // @Min(1): valida que o ID deve ser no mínimo 1 (evita IDs zero ou negativos)
    @Min(1)
    private Long id;
    
    // Campo nome do anime
    // @NotEmpty: valida que não pode ser nulo nem string vazia
    // Mensagem personalizada para erro de validação
    @NotEmpty(message = "nao permitido nome null ou vazio")
    private String name;
}

// FINALIDADE:
// DTO específico para operações de ATUALIZAÇÃO (PUT)
// Diferença do AnimePostRequestBody: inclui campo ID obrigatório

// FLUXO DE USO:
// 1. Cliente envia JSON para atualizar anime existente
// 2. Spring converte JSON para esta classe
// 3. Validações são verificadas automaticamente
// 4. ID identifica qual registro atualizar
// 5. Nome contém os novos dados

// EXEMPLO DE JSON VÁLIDO:
// {
//   "id": 5,
//   "name": "One Piece Updated"
// }

// EXEMPLOS DE JSON INVÁLIDOS:

// 1. ID nulo (viola @NotNull):
// {
//   "id": null,
//   "name": "Teste"
// }

// 2. ID zero ou negativo (viola @Min(1)):
// {
//   "id": 0,
//   "name": "Teste"
// }

// 3. Nome vazio (viola @NotEmpty):
// {
//   "id": 5,
//   "name": ""
// }

// 4. Nome nulo (viola @NotEmpty):
// {
//   "id": 5,
//   "name": null
// }

// USO NO CONTROLLER:
// @PutMapping
// public ResponseEntity<Void> update(@RequestBody @Valid AnimePutRequestBody request) {
//     // request.getId() para identificar
//     // request.getName() para novos dados
// }

// BOA PRÁTICA:
// Separar DTOs por operação (Post vs Put) permite validações específicas
// e evita campos desnecessários em cada operação