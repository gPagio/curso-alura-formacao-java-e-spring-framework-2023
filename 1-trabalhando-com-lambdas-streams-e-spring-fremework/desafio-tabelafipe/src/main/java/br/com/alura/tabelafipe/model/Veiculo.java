package br.com.alura.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record Veiculo(@JsonAlias("TipoVeiculo") Integer tipoVeiculo,
                      @JsonAlias("Valor") String valor,
                      @JsonAlias("Marca") String marca,
                      @JsonAlias("Modelo") String modelo,
                      @JsonAlias("AnoModelo") Integer anoModelo,
                      @JsonAlias("Combustivel") String combustivel,
                      @JsonAlias("CodigoFipe") String codigoFipe,
                      @JsonAlias("MesReferencia") String mesReferencia,
                      @JsonAlias("SiglaCombustivel") String siglaCombustivel) {
    @Override
    public String toString() {
        return "Tipo Veículo: " + tipoVeiculo + "\n" +
                "Valor: " + valor + "\n" +
                "Marca: " + marca + "\n" +
                "Modelo: " + modelo + "\n" +
                "Ano Modelo: " + anoModelo + "\n" +
                "Combustível: " + combustivel + "\n" +
                "Código Fipe: " + codigoFipe + "\n" +
                "Mês Referência: " + mesReferencia + "\n" +
                "Sigla Combustível: " + siglaCombustivel;
    }
}