package br.com.code.challenge.book.repository.read.all;

import br.com.code.challenge.book.converter.BookConverter;
import br.com.code.challenge.book.model.Book;
import io.r2dbc.pool.ConnectionPool;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class FindAllRepositoryImpl implements FindAllRepository {

    private final ConnectionPool connectionPool;
    private final BookConverter converter;

    public FindAllRepositoryImpl(ConnectionPool connectionPool,
                                 BookConverter converter) {
        this.connectionPool = connectionPool;
        this.converter = converter;
    }

    @Override
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
                                        converter.toEntity(row)
                                )
                        )
                )
                .collect()
                .asList();
    }
}
