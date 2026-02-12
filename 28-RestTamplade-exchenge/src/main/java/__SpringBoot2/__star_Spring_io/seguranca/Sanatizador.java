// Pacote de segurança - classes relacionadas a proteção e sanitização
package __SpringBoot2.__star_Spring_io.seguranca;

// JSoup - biblioteca para parsing e sanitização de HTML
import org.jsoup.*;
import org.jsoup.safety.Safelist;

// Import da classe de domínio do projeto
import __SpringBoot2.__star_Spring_io.dominio.Anime;

/**
 * CLASSE Sanatizador (OBSERVAÇÃO: nome deveria ser "Sanitizador" - corretor ortográfico)
 * 
 * Responsabilidade: Prevenir ataques XSS (Cross-Site Scripting) 
 * limpando entradas de usuário de conteúdo HTML/JavaScript malicioso.
 * 
 * Princípio: Nunca confie em dados de entrada do usuário.
 */
public class Sanatizador {
    
    /**
     * MÉTODO 1: Sanitiza uma string removendo qualquer código HTML/JavaScript
     * 
     * @param input - String que pode conter HTML/JavaScript perigoso
     * @return String limpa e segura ou null se input for null
     * 
     * PROCESSO DE SANITIZAÇÃO:
     * 1. Verifica se input é null → retorna null
     * 2. Remove todas as tags HTML usando JSoup
     * 3. Extrai apenas o texto puro (sem tags)
     * 4. Limita tamanho máximo para 100 caracteres
     */
    public static String saniString(String input) {
        // Passo 1: Verificação de null (fail-fast)
        if (input == null) {
            return null;
        }
        
        // Passo 2: Remove TODAS as tags HTML
        // Safelist.none() = não permite nenhuma tag, remove todas
        // Exemplo: "<script>alert('hack')</script>" → ""
        String htmlseguro = Jsoup.clean(input, Safelist.none());
        
        // Passo 3: Extrai apenas texto, convertendo entidades HTML
        // Exemplo: "&lt;script&gt;" → "<script>"
        String textoPuro = Jsoup.parse(htmlseguro).text();
        
        // Passo 4: Limita tamanho máximo (prevenção de overflow)
        if (textoPuro.length() > 100) {
            textoPuro = textoPuro.substring(0, 100);
        }
        if (textoPuro.isEmpty()) {
        	textoPuro = "CARACTERES OU SIMBOLOS INPROPRIOS";
        }
        
        return textoPuro;
    }
    
    /**
     * MÉTODO 2: Sanitiza um objeto Anime completo
     * 
     * @param anime - Objeto Anime que pode conter dados não confiáveis
     * @return Novo objeto Anime com dados sanitizados
     * 
     * IMPORTANTE: Cria NOVA instância, não modifica o original
     * (Princípio de imutabilidade)
     */
    public static Anime saniAnime(Anime anime) {
        // Passo 1: Verificação de null
        if (anime == null) return null;
        
        // Passo 2: Cria novo Anime usando Builder Pattern
        // Apenas campos necessários são sanitizados
        return Anime.builder()
            .id(anime.getId())  // ID geralmente é seguro (gerado pelo sistema)
            .name(Sanatizador.saniString(anime.getName())) // Nome é sanitizado
            .build();           // Outros campos são ignorados/definidos separadamente
    }
}

/**
 * COMO E ONDE USAR ESTA CLASSE:
 * 
 * 1. NA ENTRADA DE DADOS (antes de salvar no banco):
 *    String nomeSeguro = Sanatizador.saniString(nomeRecebido);
 *    
 * 2. NA SAÍDA DE DADOS (antes de enviar para o cliente):
 *    Anime animeSeguro = Sanatizador.saniAnime(animeDoBanco);
 * 
 * 3. EM CONTROLLERS ou SERVICES:
 *    @PostMapping
 *    public void salvar(@RequestBody Anime anime) {
 *        Anime animeLimpo = Sanatizador.saniAnime(anime);
 *        repository.save(animeLimpo);
 *    }
 */

/**
 * TIPOS DE ATAQUES PREVENIDOS:
 * 
 * 1. XSS (Cross-Site Scripting):
 *    Input: "<script>robarCookies()</script>"
 *    Saída: "" (vazio - script removido)
 * 
 * 2. Injection de HTML:
 *    Input: "<b onmouseover=alert('hack')>Clique</b>"
 *    Saída: "Clique" (sem tag e sem evento)
 * 
 * 3. Entidades HTML perigosas:
 *    Input: "&lt;script&gt;" (codificado)
 *    Saída: "<script>" (decodificado, mas tags removidas depois)
 */

/**
 * LIMITAÇÕES E CONSIDERAÇÕES:
 * 
 * 1. Tamanho fixo (100 chars): Pode cortar dados legítimos
 *    Solução: Aumentar limite ou usar limite por campo
 * 
 * 2. Campos sanitizados: Apenas 'name' no método saniAnime()
 *    Necessário adicionar outros campos se existirem
 * 
 * 3. Performance: JSoup é eficiente, mas processamento extra
 *    Considerar uso apenas em campos de texto livre
 * 
 * 4. Dados perdidos: Tags HTML legítimas também são removidas
 *    Se precisar de HTML seguro, use Safelist.basic()
 */

/**
 * ALTERNATIVAS DO JSOUP (Safelist):
 * 
 * Safelist.none()         → Remove TODAS tags (usado aqui)
 * Safelist.basic()        → Permite tags básicas: <b>, <i>, <p>, etc.
 * Safelist.simpleText()   → Apenas tags de texto: <b>, <em>, <strong>
 * Safelist.relaxed()      → Permite muitas tags (cuidado!)
 * 
 * Exemplo alternativo para comentários com formatação básica:
 * String htmlSeguro = Jsoup.clean(input, Safelist.basic());
 */

/**
 * BOAS PRÁTICAS DE SEGURANÇA:
 * 
 * 1. Sanitize na entrada E na saída (defesa em profundidade)
 * 2. Use bibliotecas testadas (JSoup) em vez de regex caseiro
 * 3. Registre tentativas de ataque (logs de segurança)
 * 4. Combine com outras proteções: CORS, CSRF, HTTPS
 * 
 * REGRA DE OURO: Todo dado de origem externa é INIMIGO até que seja provado INOFENSIVO.
 */