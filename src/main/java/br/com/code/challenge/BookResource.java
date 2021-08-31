package br.com.code.challenge;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Statement;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("/books")
public class BookResource {

    @Inject
    ConnectionPool connectionPool;

    @ConfigProperty(name = "r2dbc.book-param-prefix")
    Optional<String> bookNameParamPrefix;

    @ConfigProperty(name = "r2dbc.book-name-param-value")
    String bookNameParamValue;

    @ConfigProperty(name = "r2dbc.book-id-param-value")
    String bookIdParamValue;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Book>> findAll() {
        final var createConnectionCall = Uni.createFrom().publisher(connectionPool.create());

        return createConnectionCall
            .onItem()
            .transformToMulti(connection ->
                Multi.createFrom().publisher(
                    connection.createStatement("SELECT * FROM Books").execute())
            )
            .onItem()
            .transformToMultiAndMerge(result ->
                Multi.createFrom().publisher(
                    result.map((row, rowMetadata) ->
                        Book.builder()
                            .id(row.get("id", Integer.class))
                            .name(row.get("name", String.class))
                            .build()))
            )
            .collect().asList();
    }

    @POST
    public Uni<Response> create(@Context UriInfo uriInfo,
                                @RequestBody Book book) {
        final var prefix = bookNameParamPrefix.orElse("");
        final var createConnectionCall = Uni.createFrom().publisher(connectionPool.create());

        return createConnectionCall
            .onItem()
            .transform(connection ->
                connection.createStatement("INSERT INTO Books (name) VALUES (" + prefix + bookNameParamValue +")")
                    .bind(bookNameParamValue, book.getName())
                    .returnGeneratedValues("id"))
            .onItem()
            .transformToMulti(Statement::execute)
            .onItem()
            .transformToMultiAndMerge(result ->
                result.map((row, rowMetadata) ->
                    book.setId(row.get("id", Integer.class))
                )
            )
            .collect()
            .first()
            .onItem()
            .transform(savedBook ->
                Response.created(
                    UriBuilder.fromPath(uriInfo.getPath()).path("/{id}").build(savedBook.getId())).build()
            );
    }

    @GET
    @Path("/{id}")
    public Uni<Response> findById(@PathParam("id") Long id) {
        final var prefix = bookNameParamPrefix.orElse("");
        final var createConnectionCall = Uni.createFrom().publisher(connectionPool.create());

        return createConnectionCall
            .onItem()
            .transform(connection ->
                connection.createStatement("SELECT * FROM Books WHERE id = " + prefix + bookIdParamValue)
                    .bind(bookIdParamValue, id)
            )
            .onItem()
            .transformToMulti(Statement::execute)
            .onItem()
            .transformToMultiAndMerge(result ->
                result.map((row, rowMetadata) ->
                    Book.builder()
                        .id(row.get("id", Integer.class))
                        .name(row.get("name", String.class))
                        .build()
                )
            )
            .collect()
            .first()
            .onItem()
            .ifNull()
            .failWith(BookNotFoundException::new)
            .onItem()
            .ifNotNull()
            .transform(book ->
                Response.ok(book).build()
            );
    }
}