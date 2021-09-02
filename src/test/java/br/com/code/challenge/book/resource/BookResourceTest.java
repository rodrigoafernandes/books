package br.com.code.challenge.book.resource;

import br.com.code.challenge.book.model.Book;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BookResourceTest {

  @Test
  void givenBooksNotFound_whenSearchAllBooks_thenShouldReturnsHttpStatusOkAndEmptyJsonArray() {
    final var bookName = "Test";

    given()
            .when().get("/books/1")
            .then()
            .statusCode(SC_NOT_FOUND);

    final var noResults = given()
            .when().get("/books")
            .then()
            .statusCode(SC_OK)
            .extract()
            .body().as(new TypeRef<List<Book>>() {});

    assertTrue(isEmpty(noResults));

    final var location = given()
            .contentType(JSON)
            .body(Book.builder()
                    .name(bookName)
                    .build())
            .when()
            .post("/books")
            .then()
            .statusCode(SC_CREATED)
            .extract().header(HttpHeaders.LOCATION);

    final var books = given()
        .when().get("/books")
        .then()
        .statusCode(SC_OK)
        .extract()
        .body().as(new TypeRef<List<Book>>() {});

    assertTrue(isNotEmpty(books));

    final var book = given()
            .when().get(location)
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(Book.class);

    assertNotNull(book);
    assertEquals(bookName, book.getName());
  }

}
