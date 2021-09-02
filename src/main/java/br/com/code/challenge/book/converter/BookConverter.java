package br.com.code.challenge.book.converter;

import br.com.code.challenge.book.model.Book;
import io.r2dbc.spi.Row;

public interface BookConverter {

    Book toEntity(Row row);

}
