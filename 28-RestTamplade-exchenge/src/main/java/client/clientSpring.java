package client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import __SpringBoot2.__star_Spring_io.dominio.Anime;
import lombok.extern.log4j.Log4j2;





@Log4j2
public class clientSpring {

	public static void main(String[] args) {
		ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/2", Anime.class);
		log.info(entity);
		
		Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/2", Anime.class);
		log.info(object);
	
	}

}
