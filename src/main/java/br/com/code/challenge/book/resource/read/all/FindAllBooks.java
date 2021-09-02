package br.com.code.challenge.book.resource.read.all;

import br.com.code.challenge.book.model.Book;
import br.com.code.challenge.book.repository.read.all.FindAllRepository;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/books")
public class FindAllBooks {

    private final FindAllRepository repository;

    public FindAllBooks(FindAllRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Book>> findAll() {
        return repository.findAll();
    }
}
