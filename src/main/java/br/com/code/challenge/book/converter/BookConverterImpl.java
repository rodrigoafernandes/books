package br.com.code.challenge.book.converter;

import br.com.code.challenge.book.model.Book;
import io.r2dbc.spi.Row;

import javax.inject.Singleton;

@Singleton
public class BookConverterImpl implements BookConverter {
    @Override
    public Book toEntity(Row row) {
        return Book.builder()
                .id(row.get("id", Integer.class))
                .name(row.get("name", String.class))
                .build();
    }
}
