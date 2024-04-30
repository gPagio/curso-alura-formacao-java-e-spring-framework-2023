package br.com.alura.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MarcaCarro(@JsonAlias("codigo") Integer codigoMarca,
                         @JsonAlias("nome") String nomeMarca) {
}
