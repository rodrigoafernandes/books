package br.com.code.challenge.book.repository.create;

import br.com.code.challenge.book.model.Book;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Statement;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;

@Singleton
@UnlessBuildProfile("test")
public class CreateRepositoryOracleR2dbcImpl implements CreateRepository {

    private final ConnectionPool connectionPool;

    public CreateRepositoryOracleR2dbcImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Uni<Book> save(Book book) {
        final var createConnectionCall = Uni.createFrom().publisher(connectionPool.create());

        return createConnectionCall
                .onItem()
                .transform(connection ->
                        connection.createStatement("INSERT INTO Books (name) VALUES (:name)")
                                .bind("name", book.getName())
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
                .first();
    }
}
