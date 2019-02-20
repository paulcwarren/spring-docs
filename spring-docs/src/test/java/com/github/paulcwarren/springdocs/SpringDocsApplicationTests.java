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

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(
        classes = SpringDocsApplication.class,
        properties={"spring.profiles.active=in-memory,fs",
        			"spring.jpa.properties.hibernate.temp.use_jdbc_metadata_default=false",
        			"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"},
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

        /**
         * Uncomment to test google document classification feature
         *
         * Requires:
         *  - google private key json file and GOOGLE_APPLICATION_CREDENTIALS environment variable
         *  - Application run with the `google-classification` profile to enable the classification event handlers
         *
         * Describe("GCP Natural Language API", () -> {
         *    It("classify the document", () -> {
         *         // Instantiates a client
         *         try (LanguageServiceClient language = LanguageServiceClient.create()) {
         *             String text = "San Francisco police say three students have been taken into custody after a report of a gun on campus at Balboa High School. Police say that one person has sustained non-life threatening injuries during the incident and a firearm has been recovered.";
         *
         *          // set content to the text string
         *          Document doc = Document.newBuilder()
         *                  .setContent(text)
         *                     .setType(Document.Type.PLAIN_TEXT)
         *                     .build();
         *             ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
         *                     .setDocument(doc)
         *                     .build();
         *             // detect categories in the given text
         *             ClassifyTextResponse response = language.classifyText(request);
         *
         *             assertThat(response.getCategoriesList().length, is(not(0)));
         *        }
         *     });
         * });
         */
    }

	@Test
	public void noop() {
	}

}
