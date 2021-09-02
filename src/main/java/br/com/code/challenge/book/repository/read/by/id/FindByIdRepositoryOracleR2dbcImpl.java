package br.com.code.challenge.book.repository.read.by.id;

import br.com.code.challenge.book.converter.BookConverter;
import br.com.code.challenge.book.exception.BookNotFoundException;
import br.com.code.challenge.book.model.Book;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Statement;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;

@Singleton
@UnlessBuildProfile("test")
public class FindByIdRepositoryOracleR2dbcImpl implements FindByIdRepository {

    private final ConnectionPool connectionPool;
    private final BookConverter converter;

    public FindByIdRepositoryOracleR2dbcImpl(ConnectionPool connectionPool,
                                             BookConverter converter) {
        this.connectionPool = connectionPool;
        this.converter = converter;
    }

    @Override
    public Uni<Book> findById(Long id) {
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
                                converter.toEntity(row)
                        )
                )
                .collect()
                .first()
                .onItem()
                .ifNull()
                .failWith(BookNotFoundException::new);
    }
}
