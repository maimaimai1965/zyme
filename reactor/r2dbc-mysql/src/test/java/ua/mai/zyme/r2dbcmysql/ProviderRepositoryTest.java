package ua.mai.zyme.r2dbcmysql;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Provider;
import ua.mai.zyme.r2dbcmysql.repository.ProviderRepository;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
public class ProviderRepositoryTest {

    @Autowired
    private ProviderRepository providerRepository;

    @Value("${spring.r2dbc.url}")
    private String dbUrl;


    private void runSql(String sql) {
//        TestUtil.runSql(dbUrl, sql);
    }


    @BeforeEach
    public void setup() {
//        initializeDatabase();
//        insertData();
    }

    private void initializeDatabase() {
        String query = "CREATE TABLE IF NOT EXISTS member (id SERIAL PRIMARY KEY, name TEXT NOT NULL);";
        runSql(query);
    }

    private void insertData() {
//        Flux<Member> memberFlux = Flux.just(
//                Member.builder().name("ani").build(),
//                Member.builder().name("budi").build(),
//                Member.builder().name("cep").build(),
//                Member.builder().name("dod").build()
//        );
//        providerRepository.deleteAll()
//                .thenMany(memberFlux)
//                .flatMap(providerRepository::save)
//                .doOnNext(member -> log.info("inserted {}", member))
//                .blockLast();
    }


    @Test
    public void getOne() {
//        providerRepository.findById()
//                .uri("/api/member/ani")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody()
//                .jsonPath("$.name")
//                .isEqualTo("ani")
//                .jsonPath("$.id")
//                .isNumber();
    }


    @Test
    public void findAll() {
        Flux<Provider> all = providerRepository.findAll();
//        findById()
//                .uri("/api/member/ani")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody()
//                .jsonPath("$.name")
//                .isEqualTo("ani")
//                .jsonPath("$.id")
//                .isNumber();
    }

}
