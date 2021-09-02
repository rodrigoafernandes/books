package br.com.code.challenge.book.repository.read.by.id;

import br.com.code.challenge.book.model.Book;
import io.smallrye.mutiny.Uni;

public interface FindByIdRepository {

    Uni<Book> findById(Long id);

}
