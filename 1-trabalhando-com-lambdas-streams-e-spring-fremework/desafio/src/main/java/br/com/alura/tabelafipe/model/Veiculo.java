package br.com.alura.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Veiculo(@JsonAlias("Valor") String valor,
                      @JsonAlias("Marca") String marca,
                      @JsonAlias("Modelo") String modelo,
                      @JsonAlias("AnoModelo") Integer anoModelo,
                      @JsonAlias("Combustivel") String combustivel) {
    @Override
    public String toString() {
        return "Valor: " + valor + "\n" +
                "Marca: " + marca + "\n" +
                "Modelo: " + modelo + "\n" +
                "Ano Modelo: " + anoModelo + "\n" +
                "Combustível: " + combustivel + "\n";
    }
}