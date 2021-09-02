package br.com.code.challenge.book.repository.read.all;

import br.com.code.challenge.book.model.Book;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface FindAllRepository {

    Uni<List<Book>> findAll();

}
