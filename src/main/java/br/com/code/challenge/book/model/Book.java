package br.com.code.challenge.book.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Book implements Serializable {

  private Integer id;

  private String name;

}
