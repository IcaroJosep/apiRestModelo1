package __SpringBoot2.__star_Spring_io.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import __SpringBoot2.__star_Spring_io.dominio.Anime;
import lombok.extern.log4j.Log4j2;

@DataJpaTest(properties = { "spring.jpa.properties.jakarta.persistence.validation.mode=none" })
@Log4j2
@DisplayName("animeRepository testes")
class AnimeRepositoryTest {

	@Autowired
	private AnimeRepository animeRepository;

	@BeforeEach
	void setUp() {
		List<Anime> AnimesToSaved = List.of(
				Anime.builder().name("alex").build(),
				Anime.builder().name("barbara").build(),
				Anime.builder().name("carlos").build(),
				Anime.builder().name("daniel").build());
		for (Anime anime : AnimesToSaved) {
			animeRepository.save(anime);
		}
	}

	@Nested
	@DisplayName("Save-testes")
	class Save {

		@Test
		@DisplayName("persiste anime com sucesso quando dados validos")
		void save_persistAnimeSuccessfully_whenDataIsValid() {
			Anime animeIdNull = Anime.builder().name("kakashi").build();

			Anime animeAssert = animeRepository.saveAndFlush(animeIdNull);

			Assertions.assertThat(animeAssert).isNotNull();
			Assertions.assertThat(animeAssert.getId()).isNotNull().isPositive();
			Assertions.assertThat(animeAssert.getName()).isEqualTo(animeIdNull.getName());
		}

		@Test
		@DisplayName("lança DataIntegrityViolationException quando nome de anime for nulo")
		void save_throwsDataIntegrityViolationException_whenNameOfAnimeIsNullable() {
			Anime animeToBeSaved = new Anime();

			Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
					.isThrownBy(() -> animeRepository.saveAndFlush(animeToBeSaved));
		}
	}

	@Nested
	@DisplayName("findAll Paginado - testes ")
	class FindAllPagination {

		@Test
		@DisplayName("retorna Page de animes com pageble padrao")
		void findAll_returnPageSuccessfully_whenPgeableDefalt() {

			Page<Anime> listOfAnime = animeRepository.findAll(Pageable.ofSize(5));
			
			Assertions.assertThat(listOfAnime.getContent().size()).isEqualTo(4);
			Assertions.assertThat(listOfAnime.getTotalPages()).isEqualTo(1);
			Assertions.assertThat(listOfAnime.getNumber()).isEqualTo(0);
			
		}
		
		@Test
	    @Disabled("TODO: Implementar defesa em profundidade com Timeout de 2000ms para mitigar ataques DoS")
	    @DisplayName("Deve interromper a consulta se o processamento exceder 2 segundos")
	    void findAll_ShouldTerminate_WhenQueryExceedsTimeout() {
	        // Este teste será implementado após configurar 'spring.jpa.properties.jakarta.persistence.query.timeout'
		/*Futuro:
		 * AJUSTE:mudar a .propriets para introduzir timeout em banco de dados
		 * 
		 * MOTIVO:introduzir defeza em profundidade garantindo a proteçao de 
		 * prcesamento sobre ataque Ddos em uma requisiçao que chege a o banco
		 * 
		 * INPLEMENTAÇAO : teste de time alte sobre procesamento de findAll<pageable>
		 * 
		 * EXPECIFICAÇAO: garantir q se pageble for enviado com arqumentos maliciosos 
		 * o procesamento sera interompido com 2000 milisegundos ,assim mitigando o ataque!
		 * */
		}
	}
	
	@Nested
	@DisplayName("findByNameContaining paginado - testes")
	class findByNameContaining{
		
		@Test
		@DisplayName("retona pagina de animes que comtenhao a sequencia de caracteres")
		void findByNameContaining_retunePageOfAnimeSuccesfully_whenPatternMatches() {
			Page<Anime> listContaining=animeRepository.findByNameContaining("ar",PageRequest.ofSize(5) );
		
			log.info(listContaining.getContent());
			Assertions.assertThat(listContaining.getContent()).isNotEmpty();
			Assertions.assertThat(listContaining.getTotalElements()).isEqualTo(2);
			Assertions.assertThat(listContaining.getContent())
								.extracting(Anime::getName)
								.containsExactlyInAnyOrder("barbara","carlos");
		}	
		
		@Test
		@DisplayName("Retorna uma página de animes vazia quando a sequência de caracteres não é encontrada ")
		void findByNameContaining_ReturnsEmptyPage_WhenPatternDoesNotMatch() {
			Page<Anime> listContaining=animeRepository.findByNameContaining("zz",PageRequest.ofSize(5) );
		
			log.info(listContaining.getContent());
			Assertions.assertThat(listContaining.getContent()).isEmpty();
			Assertions.assertThat(listContaining.getTotalElements()).isEqualTo(0);	
		}
	}
	
	@Nested
	@DisplayName("findByName paginado - testes")
	class findByName{
		
		@Test
		@DisplayName("retorna pagina de anime comtendo correspondencia exatas")
		void findByName_returnesAnimePageNotEmpty_whenThereIsExactMatch(){	
			animeRepository.saveAndFlush(Anime.builder().name("barb").build());
			
			Page<Anime> PageAnime = animeRepository.findByName("barb", PageRequest.ofSize(5));
		
			log.info(PageAnime);
			Assertions.assertThat(PageAnime.getContent()).isNotEmpty();
			Assertions.assertThat(PageAnime.getTotalElements()).isEqualTo(1);
			Assertions.assertThat(PageAnime.getTotalPages()).isEqualTo(1);
			Assertions.assertThat(PageAnime.getContent().get(0).getName()).isEqualTo("barb");
		}
		
		@Test
		@DisplayName("retorna pagina de anime vazia quando nao encomtra correspondencia exatas")
		void findByName_returnesAnimePageEmpty_whenThereAreNoExactMatches(){	
			
			
			Page<Anime> PageAnime = animeRepository.findByName("barb", PageRequest.ofSize(5));
		
			log.info(PageAnime);
			Assertions.assertThat(PageAnime.getContent()).isEmpty();
			Assertions.assertThat(PageAnime.getTotalElements()).isEqualTo(0);
			Assertions.assertThat(PageAnime.getTotalPages()).isEqualTo(0);
			
		}
	}
	
	@Nested
	@DisplayName("delete - testes")
	class delete{
		
		@Test
		@DisplayName("remove entidade no banco apartir de uma entidade fornecida quando bem sucedido")
		void delete_RemovesEntityFromDatabase_WhenTheProvidedEntityWasFound() {
			Optional<Anime> obj = animeRepository.findById(1l);
			
			Assertions.assertThat(obj).isNotEmpty();
			
			Anime animeToDelete =obj.get();
			
			animeRepository.delete(animeToDelete);
			
			Optional<Anime> animeVerify = animeRepository.findById(animeToDelete.getId());
			
			Assertions.assertThat(animeVerify).isEmpty();
		}
		
		@Test
		@DisplayName("Não altera o banco de dados ao tentar deletar uma entidade com ID inexistente")
		void  delete_DoesNotChangeDatabase_WhenEntityIdNotFound() {
			long countBefore = animeRepository.count();
			
			animeRepository.delete(Anime.builder().name("kaka").id(99L).build());
			
			long countAfter = animeRepository.count();
			
			Assertions.assertThat(countBefore).isEqualTo(countAfter);
			
		}
		
	}
}
















