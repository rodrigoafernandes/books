package br.com.code.challenge;

import org.apache.http.HttpStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BookNotFoundExceptionHandler implements ExceptionMapper<BookNotFoundException> {
  @Override
  public Response toResponse(BookNotFoundException e) {
    return Response.status(HttpStatus.SC_NOT_FOUND).build();
  }
}
