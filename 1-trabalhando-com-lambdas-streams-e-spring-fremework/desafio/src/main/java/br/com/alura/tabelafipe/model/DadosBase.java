package br.com.alura.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosBase(@JsonAlias("codigo") String codigo,
                        @JsonAlias("nome") String nome) {
    @Override
    public String toString() {
        return "Código: " + codigo + ", nome: " + nome;
    }
}
