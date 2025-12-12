// PRIMEIRA: CLASSE BASE DE DETALHES DE EXCEÇÃO
package __SpringBoot2.__star_Spring_io.exception;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.SuperBuilder;

// @Data: Gera getters, setters, toString, equals, hashCode
// @SuperBuilder: Permite usar padrão Builder com herança
@Data
@SuperBuilder
public class ExceptionDetails {
    protected int status;           // Código HTTP (ex: 400, 404, 500)
    protected String details;       // Descrição detalhada do erro
    protected String title;         // Título do erro (ex: "Bad Request")
    protected String developerMessage; // Mensagem técnica para desenvolvedores
    protected LocalDateTime timestamp; // Data/hora quando erro ocorreu
}

// FINALIDADE: Classe base para padronizar respostas de erro da API
// USO: Todas as respostas de erro seguirão este formato JSON

// EXEMPLO DE JSON GERADO:
// {
//   "status": 400,
//   "details": "Requisição inválida - campo 'name' vazio",
//   "title": "Bad Request",
//   "developerMessage": "br.com.erro.BedRequestException: Campo name não pode ser vazio",
//   "timestamp": "2024-01-15T10:30:00"
// }