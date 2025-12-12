// Pacote para classes de exceção personalizadas
package __SpringBoot2.__star_Spring_io.exception;

// Importações do Spring para manipulação de status HTTP
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus: Define qual status HTTP retornar quando esta exceção for lançada
// HttpStatus.BAD_REQUEST = 400 (requisição inválida)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BedRequestException extends RuntimeException { // OBS: Nome deveria ser "BadRequestException"
    
    // Construtor que recebe mensagem de erro
    public BedRequestException(String messagen) { // OBS: Parâmetro "messagen" deveria ser "message"
        super(messagen); // Chama construtor da classe pai (RuntimeException)
    }
}

// FINALIDADE:
// Exceção personalizada para representar erros de requisição inválida (HTTP 400)

// QUANDO USAR:
// Lançar esta exceção quando o cliente enviar dados inválidos que não são capturados pelas validações automáticas

// EXEMPLOS DE USO:
// 1. Dados inconsistentes:
//    if (anoLancamento > 2024) {
//        throw new BedRequestException("Ano de lançamento inválido");
//    }
//
// 2. Lógica de negócio violada:
//    if (anime.estaInativo()) {
//        throw new BedRequestException("Não é possível modificar anime inativo");
//    }

// FLUXO QUANDO LANÇADA:
// 1. Exceção é lançada no service/controller
// 2. Spring captura (por ser @ResponseStatus)
// 3. Retorna automaticamente HTTP 400 com a mensagem
// 4. Cliente recebe: Status 400 + corpo com detalhes do erro

// ALTERNATIVA MAIS COMPLETA:
/*
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private String campo;
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String campo, String message) {
        super(message);
        this.campo = campo;
    }
    
    public String getCampo() {
        return campo;
    }
}
*/

// BOAS PRÁTICAS:
// 1. Nome correto: "BadRequestException" (com "a" em vez de "e")
// 2. Mensagens claras para o cliente
// 3. Usar em conjunto com validações automáticas (@Valid)
// 4. Documentar quando cada exceção é lançada

// TRATAMENTO GLOBAL (opcional):
// Pode ser combinado com @ControllerAdvice para tratamento centralizado de exceções