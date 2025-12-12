// Pacote para manipuladores de exceção
package __SpringBoot2.__star_Spring_io.handler;

// Importações para data/hora e manipulação de listas
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Importações Spring para tratamento de HTTP
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Importações das exceções e detalhes personalizados
import __SpringBoot2.__star_Spring_io.exception.BedRequestException;
import __SpringBoot2.__star_Spring_io.exception.BedRequestExceptionDetails;
import __SpringBoot2.__star_Spring_io.exception.ExceptionDetails;
import __SpringBoot2.__star_Spring_io.exception.ValidationException;
// Lombok para logs
import lombok.extern.log4j.Log4j2;

// @Log4j2: Gera logger automático (log.info(), log.error())
// @ControllerAdvice: Define classe como manipulador global de exceções
@Log4j2
@ControllerAdvice // GERAL: Captura exceções de todos os controllers
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    // Herda métodos prontos para tratar exceções Spring MVC
    
    // ========== TRATA BadRequestException PERSONALIZADA ==========
    // @ExceptionHandler: Define que este método trata BedRequestException
    @ExceptionHandler(BedRequestException.class)
    public ResponseEntity<BedRequestExceptionDetails> handlerBedResponseException(BedRequestException bre) {
        // Log do erro para debugging
        log.error("Bad request exception: {}", bre.getMessage());
        
        // Constrói resposta padronizada
        return new ResponseEntity<>(
            BedRequestExceptionDetails.builder()
                .timestamp(LocalDateTime.now())        // Momento do erro
                .status(HttpStatus.BAD_REQUEST.value()) // HTTP 400
                .title("bed request exception , check the documentation") // Título
                .details(bre.getMessage())             // Mensagem da exceção
                .developerMessage(bre.getClass().getName()) // Classe da exceção
                .build(),
            HttpStatus.BAD_REQUEST // Status HTTP
        );
    }
    
    // ========== TRATA ERROS DE VALIDAÇÃO (@Valid) ==========
    // Sobrescreve método da classe pai para validações falhadas
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, // Exceção de validação
            HttpHeaders headers,                       // Cabeçalhos HTTP
            HttpStatusCode status,                     // Status code
            WebRequest request) {                      // Request completo
            
        // Extrai todos os erros de campos da exceção
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        
        // Junta nomes dos campos com erro (ex: "name, email, age")
        String fields = fieldErrors.stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        
        // Junta mensagens de erro (ex: "Não pode ser vazio, Email inválido")
        String fieldsMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        // Log dos erros de validação
        log.warn("Validation errors - Fields: {}, Messages: {}", fields, fieldsMessage);
            
        // Retorna resposta com detalhes específicos de validação
        return new ResponseEntity<>(
            ValidationException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()) // Sempre 400 para validação
                .title("bed request exception , invalid fields")
                .details(exception.getMessage())        // Mensagem geral da exceção
                .developerMessage(exception.getClass().getName())
                .fields(fields)                         // Campos problemáticos
                .fieldsMessage(fieldsMessage)           // Mensagens específicas
                .build(),
            HttpStatus.BAD_REQUEST
        );
    }
    
    // ========== TRATA OUTRAS EXCEÇÕES INTERNAS DO SPRING ==========
    // Método genérico para exceções não tratadas especificamente
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,                   // Exceção ocorrida
            @Nullable Object body,          // Corpo original (pode ser nulo)
            HttpHeaders headers,           // Cabeçalhos HTTP
            HttpStatusCode statusCode,     // Status code apropriado
            WebRequest request) {          // Request
            
        // Log de erro genérico
        log.error("Internal exception: {}", ex.getMessage(), ex);
        
        // Constrói resposta base genérica
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(statusCode.value())         // Usa status do parâmetro
                .title(ex.getCause() != null ? ex.getCause().getMessage() : "erro inesperado")
                .details(ex.getMessage())           // Mensagem da exceção
                .developerMessage(ex.getClass().getName()) // Classe para debug
                .build();
        
        // Retorna resposta padronizada
        return new ResponseEntity<>(exceptionDetails, headers, statusCode);
    }
}

// RESUMO DOS TRATAMENTOS:
// 1. BedRequestException → BedRequestExceptionDetails (personalizado)
// 2. MethodArgumentNotValidException → ValidationException (validação @Valid)
// 3. Outras exceções Spring → ExceptionDetails (genérico)

// FLUXO DE ERRO TÍPICO:
// 1. Cliente faz requisição inválida
// 2. Spring lança exceção no controller/service
// 3. RestExceptionHandler captura automaticamente
// 4. Constrói resposta padronizada
// 5. Retorna JSON estruturado + status HTTP apropriado

// EXEMPLO DE RESPOSTA DE VALIDAÇÃO:
// {
//   "timestamp": "2024-01-15T10:30:00",
//   "status": 400,
//   "title": "bed request exception , invalid fields",
//   "details": "Validation failed...",
//   "developerMessage": "MethodArgumentNotValidException",
//   "fields": "name, email",
//   "fieldsMessage": "Não pode ser vazio, Email inválido"
// }

// MELHORIAS POSSÍVEIS:
// 1. Adicionar @ExceptionHandler para outras exceções (NotFoundException, etc.)
// 2. Internacionalizar mensagens de erro
// 3. Adicionar logging mais detalhado
// 4. Tratar exceções de banco de dados específicas