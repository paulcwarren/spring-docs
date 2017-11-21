package com.github.paulcwarren.springdocs;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.github.paulcwarren.springdocs.repositories.jpa.JpaDocumentRepository;
import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(
        classes = SpringDocsApplication.class,
        properties={"spring.profiles.active=in-memory,fs"},
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableJpaRepositories(basePackageClasses = JpaDocumentRepository.class)
public class SpringDocsApplicationTests {

	@Value("${local.server.port}")
	int port;

	private String document;

    {
        Describe("spring-docs", () -> {
            BeforeEach(() -> {
                RestAssured.port = port;
            });
            Context("given a Document entity", () -> {
                BeforeEach(() -> {
                    String location =
                    given()
                        .body("{\"title\": \"test1.docx\", \"author\":\"someone\"}")
                        .contentType("application/json")
                    .when()
                        .post("/documents")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract().header("Location");

                    assertThat(location).isNotEmpty();
                    int lastIndexOf = location.lastIndexOf('/');
                    document = location.substring(lastIndexOf);
                });
                Context("given that Document entity has content", () -> {
                    BeforeEach(() -> {
                        given()
                            .body("Hello Spring Docs World!")
                            .contentType("text/plain")
                        .when()
                            .put("/documentscontent" + document)
                        .then()
                            .statusCode(HttpStatus.SC_OK);
                    });
                    It("should return the content in its original format", () -> {
                        given()
                                .header("accept", "text/plain")
                        .when()
                            .get("/documentscontent" + document)
                        .then()
                            .statusCode(HttpStatus.SC_OK);
                    });
                    It("should return the content as a rendition", () -> {
                        given()
                            .header("accept", "image/jpeg")
                        .when()
                            .get("/documentscontent" + document)
                        .then()
                            .statusCode(HttpStatus.SC_OK)
                            .header("Content-Type", is("image/jpeg"));
                    });
                });
            });
        });
    }

	@Test
	public void noop() {
	}

}
