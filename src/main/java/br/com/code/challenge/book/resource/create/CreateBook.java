package br.com.code.challenge.book.resource.create;

import br.com.code.challenge.book.model.Book;
import br.com.code.challenge.book.repository.create.CreateRepository;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("/books")
public class CreateBook {

    private final CreateRepository repository;

    public CreateBook(CreateRepository repository) {
        this.repository = repository;
    }

    @POST
    public Uni<Response> createBook(@Context UriInfo uriInfo,
                             @RequestBody Book book) {
        final var createBookCall = repository.save(book);

        if (createBookCall != null) {
            return createBookCall
                    .onItem()
                    .transform(createdBook ->
                            Response.created(
                            UriBuilder.fromPath(uriInfo.getPath()).path("/{id}").build(createdBook.getId())).build()
                    );
        }

        return Uni.createFrom().item(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }
}
