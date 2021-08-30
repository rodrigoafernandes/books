package br.com.code.challenge;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Statement;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/books")
public class BookResource {

    @Inject
    ConnectionPool connectionPool;

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
                            .id(row.get("id", Long.class))
                            .name(row.get("name", String.class))
                            .build()))
            )
            .collect().asList();
    }

    @POST
    public Uni<Response> create(@Context UriInfo uriInfo,
                                @RequestBody Book book) {
        final var createConnectionCall = Uni.createFrom().publisher(connectionPool.create());

        return createConnectionCall
            .onItem()
            .transform(connection ->
                connection.createStatement("INSERT INTO Books (name) VALUES (:bookName)")
                    .bind("bookName", book.getName())
                    .returnGeneratedValues("id"))
            .onItem()
            .transformToMulti(Statement::execute)
            .onItem()
            .transformToMultiAndMerge(result ->
                result.map((row, rowMetadata) ->
                    book.setId(row.get("id", Long.class))
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
        final var createConnectionCall = Uni.createFrom().publisher(connectionPool.create());

        return createConnectionCall
            .onItem()
            .transform(connection ->
                connection.createStatement("SELECT * FROM Books WHERE id = :id")
                    .bind("id", id)
            )
            .onItem()
            .transformToMulti(Statement::execute)
            .onItem()
            .transformToMultiAndMerge(result ->
                result.map((row, rowMetadata) ->
                    Book.builder()
                        .id(row.get("id", Long.class))
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