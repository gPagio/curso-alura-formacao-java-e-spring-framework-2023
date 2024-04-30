package br.com.alura.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ModeloCarro(@JsonAlias("codigo") Integer codigoModelo,
                         @JsonAlias("nome") String nomeModelo) {
}
