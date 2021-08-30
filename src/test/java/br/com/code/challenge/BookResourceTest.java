package br.com.code.challenge;

import io.quarkus.test.junit.QuarkusTest;
import io.r2dbc.pool.ConnectionPool;
import io.restassured.common.mapper.TypeRef;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class BookResourceTest {

  @Inject
  ConnectionPool connectionPool;

  @BeforeEach
  void setUp() {
    new Flyway(Flyway.configure().dataSource())
  }

  @Test
  void givenBooksNotFound_whenSearchAllBooks_thenShouldReturnsHttpStatusOkAndEmptyJsonArray() {
    final var books = given()
        .when().get("/books")
        .then()
        .statusCode(SC_OK)
        .extract()
        .body().as(new TypeRef<List<Book>>() {
        });

    assertTrue(isEmpty(books));
  }

}
