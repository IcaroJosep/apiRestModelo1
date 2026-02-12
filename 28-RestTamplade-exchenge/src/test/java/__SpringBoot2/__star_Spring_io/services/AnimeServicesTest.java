package __SpringBoot2.__star_Spring_io.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.lang.annotation.Documented;
import java.util.List;

import org.apache.catalina.mapper.Mapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import __SpringBoot2.__star_Spring_io.dominio.Anime;
import __SpringBoot2.__star_Spring_io.mapper.AnimeMapper;
import __SpringBoot2.__star_Spring_io.repository.AnimeRepository;
import __SpringBoot2.__star_Spring_io.requests.AnimeResponse;
import __SpringBoot2.__star_Spring_io.seguranca.PageValid;
import __SpringBoot2.__star_Spring_io.seguranca.PageableValidation;

@ExtendWith(MockitoExtension.class)
class AnimeServicesTest {
		@InjectMocks
		private AnimeServices animeServices;
		
		@Mock
		private AnimeRepository animeRepository;
		
		@Spy
		private AnimeMapper animeMapper = Mappers.getMapper(AnimeMapper.class);
		
		
		@Nested
		@DisplayName("listAll paginado - testes")
		class listAll{
			
			@BeforeEach
			void setUp(){
				BDDMockito.when(animeRepository.findAll(any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(
						Anime.builder().id(1L).name("alemcar<Script>").build(),
						Anime.builder().id(2L).name("felipe").build(),
						Anime.builder().id(3L).name("alfredo").build(),
						Anime.builder().id(4L).name("bernado").build(),
						Anime.builder().id(5L).name("barbara").build()
						)));
			};
			
			@Test
			@DisplayName("listAll : retorna pagina de AnimeResponse quando o \"Pageable\" for valido")
			void listAll_RetunesPageOfAnimeResponse_WhenPageableIsValid() {
				
				
				Page<AnimeResponse> resposta= animeServices.listAll(PageRequest.ofSize(5));
				
				Assertions.assertThat(resposta.getContent()).isNotEmpty();
				Assertions.assertThat(resposta.getTotalPages()).isEqualTo(1);
				Assertions.assertThat(resposta.getNumberOfElements()).isEqualTo(5);
				Assertions.assertThat(resposta.getSize()).isEqualTo(5);
	        
				
			}
			
			@Nested
			@DisplayName("Camada de Integridade: Validação de Pageable e Sanatização de Dados")
			class secValidationAndSanitizer{
				
				
				/**
			     * <b>Cenário:</b> Proteção contra ataques de negação de serviço (DoS) via paginação.
			     * <br>
			     * <b>Dado que:</b> Um usuário envie um {@code PageSize} abusivo e um {@code Sort} com injeção de script.
			     * <br>
			     * <b>Então:</b> O sistema deve sanitizar para o limite seguro de 50 itens e ordenar pelo ID.
			     * @author kendi
			     * @see PageableValidation
			     */
				@Test
			    @DisplayName("Deve retornar Pageable com limites corrigidos quando receber parâmetros abusivos")
			    void listAll_AdjustsToSafePageable_WhenInputIsHighRisk() {
						
					Pageable pageableAbusiva = PageRequest.of(
							0,
							999999,
							Sort.Direction.DESC,"password<H1><script>"
							);
	
					ArgumentCaptor<Pageable> captura = ArgumentCaptor.forClass(Pageable.class);
					
					
					animeServices.listAll(pageableAbusiva);
					
					
					BDDMockito.verify(animeRepository).findAll(captura.capture());
					Pageable resultado = captura.getValue();
					
					
					Assertions.assertThat(resultado.getPageSize()).isEqualTo(50);
					Assertions.assertThat(resultado.getSort().getOrderFor("id")).isNotNull();
					
				}
				
				
				/**
				 * 
				 */
				@Test
				void testeRetornoPageValidValidaSanitizaPageAnime() {
					
					BDDMockito.when(animeRepository.findAll(any(Pageable.class)))
							.thenReturn( new PageImpl<Anime>(List.of(
									Anime.builder().id(1L)
										 .name("alemcar<script>alert('xss')</script>")
										 .build(), 				// Script direto
							        Anime.builder().id(2L)
							        	 .name("<img src=x onerror=alert(1)>")
							        	 .build(),       		// XSS via atributo de imagem
							        Anime.builder().id(3L)
							        	 .name("<h1>alfredo</h1>")
							        	 .build(),              // Tag de estilo (HTML)
							        Anime.builder().id(4L)
							         	 .name("javascript:alert('malware')")
							         	 .build(),              // Protocolo perigoso
							        Anime.builder().id(5L)
							        	 .name("<a href='http://site-malicioso.com'>Click</a>")
							        	 .build() ,				// Link externo
							        Anime.builder().id(6L)
							        	 .name("<H1><Script></Script></H1>")
							        	 .build() 				// sem nome
							        	 
							        )));	
					
					Page<Anime> pageValidado = 
							PageValid.ValidaSanitizaPageAnime(
									animeRepository.findAll(PageRequest.ofSize(6))
									);
					
					
					List<Anime> listaSanitizada = pageValidado.getContent();
				



					// 1. Verifica se a tag <script> foi removida ou o texto foi limpo
					Assertions.assertThat(listaSanitizada.get(0).getName())
					    .doesNotContain("<script>")
					    .isEqualTo("alemcar"); // Depende de como sua lógica limpa (se remove a tag ou o conteúdo todo)

					// 2. Verifica se eventos 'onerror' foram neutralizados
					Assertions.assertThat(listaSanitizada.get(1).getName())
					    .doesNotContain("onerror")
					    .doesNotContain("<img");

					// 3. Verifica se tags H1 foram removidas (se sua política for texto puro)
					Assertions.assertThat(listaSanitizada.get(2).getName())
					    .doesNotContain("<h1>")
					    .isEqualTo("alfredo");

					// 4. Verifica protocolos de link perigosos
					Assertions.assertThat(listaSanitizada.get(3).getName())
					    .doesNotContain("javascript");
					
					Assertions.assertThat(listaSanitizada.get(4).getName())
				    .doesNotContain("Click");

					Assertions.assertThat(listaSanitizada.get(5).getName())
				    .doesNotContain("CARACTERES OU SIMBOLOS INPROPRIOS");

					
					// 5. Garantir que o tamanho da página continua o mesmo (sanitizou, não deletou o registro)
					Assertions.assertThat(pageValidado.getTotalElements()).isEqualTo(6);
					
					
				}
				
			}
			
		}
		
		
		
}









