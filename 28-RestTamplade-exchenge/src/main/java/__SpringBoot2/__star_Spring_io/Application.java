// Pacote onde a classe está localizada
// Define organização hierárquica do código
package __SpringBoot2.__star_Spring_io;

// Importa classes necessárias do Spring Boot
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Anotação principal do Spring Boot - combina 3 anotações:
// 1. @Configuration: Define classe como fonte de configuração
// 2. @EnableAutoConfiguration: Habilita configuração automática
// 3. @ComponentScan: Procura componentes no pacote e subpacotes
@SpringBootApplication
public class Application {

    // Método principal - ponto de entrada da aplicação Java
    // Executado pela JVM ao iniciar o programa
    public static void main(String[] args) {
       
    	// Inicia a aplicação Spring Boot
        // Parâmetros:
        // 1. Application.class: Classe de configuração principal
        // 2. args: Argumentos passados pela linha de comando
        SpringApplication.run(Application.class, args);
    }
}

// RESUMO DO FUNCIONAMENTO:
// 1. JVM executa main()
// 2. SpringApplication.run() inicia o Spring Boot
// 3. @SpringBootApplication configura automaticamente:
//    - Cria ApplicationContext (container Spring)
//    - Configura servidor web embutido (porta 8080)
//    - Escaneia e registra @Components, @Services, @Controllers
// 4. Aplicação fica rodando até receber comando para parar

// COMO USAR:
// 1. Executar pela IDE: Run Application.java
// 2. Linha de comando: java -jar app.jar
// 3. Maven: mvn spring-boot:run

// O QUE ACONTECE QUANDO RODA:
// - Spring Boot inicia servidor Tomcat/Jetty
// - Aplicação fica disponível em http://localhost:8080
// - Log mostra: "Started Application in X seconds"