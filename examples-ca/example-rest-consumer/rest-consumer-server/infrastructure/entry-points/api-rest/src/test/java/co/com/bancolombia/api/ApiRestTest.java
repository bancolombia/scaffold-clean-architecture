package co.com.bancolombia.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {

    @Autowired
    private WebApplicationContext context;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void shouldReturnListOfUsersVersion1() {
        client.get()
                .uri("/api/1.0/list-users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("Juan")
                .jsonPath("$[0].age").isEqualTo(25)
                .jsonPath("$[1].name").isEqualTo("Pedro")
                .jsonPath("$[1].age").isEqualTo(35)
                .jsonPath("$[2].name").isEqualTo("Santiago")
                .jsonPath("$[2].age").isEqualTo(25);
    }

    @Test
    void shouldReturnListOfUsersVersion2() {
        client.get()
                .uri("/api/2.0/list-users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("Carlos")
                .jsonPath("$[0].age").isEqualTo(21)
                .jsonPath("$[1].name").isEqualTo("Andres")
                .jsonPath("$[1].age").isEqualTo(20)
                .jsonPath("$[2].name").isEqualTo("Diego")
                .jsonPath("$[2].age").isEqualTo(40);
    }

    @Test
    void shouldReturnSumOfTwoNumbers() {
        int x = 5;
        int z = 10;
        int expectedSum = 15;

        client.get()
                .uri("/api/1.0/sum/{x}/{z}", x, z)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Integer.class).isEqualTo(expectedSum);
    }

    @Test
    void shouldReturnSumWithNegativeNumbers() {
        int x = -5;
        int z = 10;
        int expectedSum = 5;

        client.get()
                .uri("/api/1.0/sum/{x}/{z}", x, z)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Integer.class).isEqualTo(expectedSum);
    }

    @Test
    void shouldReturnSumWithZero() {
        int x = 0;
        int z = 0;
        int expectedSum = 0;

        client.get()
                .uri("/api/1.0/sum/{x}/{z}", x, z)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Integer.class).isEqualTo(expectedSum);
    }
}