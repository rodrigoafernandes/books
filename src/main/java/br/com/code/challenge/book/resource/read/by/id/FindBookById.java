package br.com.code.challenge.book.resource.read.by.id;

import br.com.code.challenge.book.model.Book;
import br.com.code.challenge.book.repository.read.by.id.FindByIdRepository;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/books")
public class FindBookById {

    private final FindByIdRepository repository;

    public FindBookById(FindByIdRepository repository) {
        this.repository = repository;
    }

    @GET
    @Path("/{id}")
    public Uni<Book> findById(@PathParam("id") Long id) {
        return repository.findById(id);
    }
}
