package __SpringBoot2.__star_Spring_io.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import __SpringBoot2.__star_Spring_io.dominio.Anime;
import lombok.extern.log4j.Log4j2;

@DataJpaTest
@Log4j2
class AnimeRepositoryTest {

	@Autowired
	private AnimeRepository animeRepository;
	
	@Nested
	@DisplayName("casos com retono de sucesso")
	class sucessCase{
		
		
		@Test
		@DisplayName("retorna sucesso caso haja a persistencia")
		void save () {
			Anime animeIdNull = Anime.builder().name("kakashi").build();
			Assertions.assertThat(animeIdNull.getId()).isNull();
			
			Anime animeAssert = animeRepository.save(animeIdNull);
			
			Assertions.assertThat(animeAssert).isNotNull();
			Assertions.assertThat(animeAssert.getId()).isNotNull().isPositive();
			Assertions.assertThat(animeAssert.getName()).isEqualTo(animeIdNull.getName());
			
		}
		
		
		
		
		
	}
	
}
