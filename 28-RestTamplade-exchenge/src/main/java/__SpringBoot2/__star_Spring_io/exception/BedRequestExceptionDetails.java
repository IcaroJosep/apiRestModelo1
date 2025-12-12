// SEGUNDA: DETALHES ESPECÍFICOS PARA BAD REQUEST
package __SpringBoot2.__star_Spring_io.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

// @Getter: Apenas getters (sem setters) - objetos de erro são imutáveis
// @SuperBuilder: Herda funcionalidade de builder da classe pai
@Getter
@SuperBuilder
public class BedRequestExceptionDetails extends ExceptionDetails {
    // Herda todos os campos da classe pai
    // Não adiciona campos novos - apenas especializa para erros 400
}

// FINALIDADE: Especialização para erros HTTP 400 (Bad Request)
// USO: Quando cliente envia dados inválidos

// EXEMPLO DE USO NO @ControllerAdvice:
// @ExceptionHandler(BedRequestException.class)
// public ResponseEntity<BedRequestExceptionDetails> handleBadRequest(...)