package br.com.code.challenge.book.repository.create;

import br.com.code.challenge.book.model.Book;
import io.smallrye.mutiny.Uni;

public interface CreateRepository {

    Uni<Book> save(Book book);

}
