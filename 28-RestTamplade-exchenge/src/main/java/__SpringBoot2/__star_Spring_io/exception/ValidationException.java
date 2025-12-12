// TERCEIRA: DETALHES PARA ERROS DE VALIDAÇÃO
package __SpringBoot2.__star_Spring_io.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationException extends ExceptionDetails {
    private final String fields;        // Nome(s) do(s) campo(s) com erro
    private final String fieldsMessage; // Mensagem(s) de erro específica(s) do campo
    
    // Campos 'final': só podem ser setados no construtor/builder - imutáveis
}

// FINALIDADE: Resposta especializada para erros de validação (@Valid)
// DIFERENÇA: Inclui campos específicos sobre quais atributos falharam

// EXEMPLO DE JSON GERADO:
// {
//   "status": 400,
//   "details": "Validation failed for object='animePostRequestBody'",
//   "title": "Bad Request",
//   "developerMessage": "org.springframework.validation.BindException",
//   "timestamp": "2024-01-15T10:30:00",
//   "fields": "name",
//   "fieldsMessage": "Nome deve ter entre 3 e 100 caracteres"
// }

// PARA MÚLTIPLOS ERROS (melhoria futura):
// private List<String> fields;
// private List<String> fieldsMessage;

// HIERARQUIA DAS CLASSES:
// ExceptionDetails (base)
//     ├── BedRequestExceptionDetails (erros gerais 400)
//     └── ValidationException (erros de validação com campos específicos)

// PADRÃO BUILDER (graças ao @SuperBuilder):
// ValidationException.builder()
//     .status(400)
//     .title("Bad Request")
//     .fields("name")
//     .fieldsMessage("Não pode ser vazio")
//     .timestamp(LocalDateTime.now())
//     .build();