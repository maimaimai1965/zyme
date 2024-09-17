package ua.mai.zyme.r2dbcmysql;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.repository.ProviderRepository;

import static org.hamcrest.Matchers.containsInAnyOrder;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
class ProviderControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ProviderRepository providerRepository;

  @Value("${spring.r2dbc.url}")
  private String dbUrl;


  @BeforeEach
  public void setup() {
    initializeDatabase();
    insertData();
  }

  private void initializeDatabase() {
    ConnectionFactory connectionFactory = ConnectionFactories.get(dbUrl);
    R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
    String query = "CREATE TABLE IF NOT EXISTS member (id SERIAL PRIMARY KEY, name TEXT NOT NULL);";
    template.getDatabaseClient().sql(query).fetch().rowsUpdated().block();
  }

  private void insertData() {
//    Flux<Member> memberFlux = Flux.just(
//        Member.builder().name("ani").build(),
//        Member.builder().name("budi").build(),
//        Member.builder().name("cep").build(),
//        Member.builder().name("dod").build()
//    );
//    providerRepository.deleteAll()
//        .thenMany(memberFlux)
//        .flatMap(providerRepository::save)
//        .doOnNext(member -> log.info("inserted {}", member))
//        .blockLast();
  }

  @Test
  public void getAll() {
    webTestClient.get()
            .uri("/api/member")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[*].name")
            .value(containsInAnyOrder("ani", "budi", "cep", "dod"));
  }


  @Test
  public void getOne() {
    webTestClient.get()
        .uri("/api/member/ani")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("ani")
        .jsonPath("$.id")
        .isNumber();
  }
}
