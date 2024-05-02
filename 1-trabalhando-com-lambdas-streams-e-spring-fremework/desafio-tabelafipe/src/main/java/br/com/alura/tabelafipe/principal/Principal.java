package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.DadosBase;
import br.com.alura.tabelafipe.model.ModeloVeiculo;
import br.com.alura.tabelafipe.model.Veiculo;
import br.com.alura.tabelafipe.service.ConsumoAPI;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private static Scanner teclado = new Scanner(System.in);
    private static ConsumoAPI consumoAPI = new ConsumoAPI();
    private static ConverteDados converteDados = new ConverteDados();

    private static final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    private Principal() {
    }

    public static void executaConsultaTabelaFipe() {
        System.out.print("""
                Escolha um tipo de veículo para buscar
                
                >>> Moto
                >>> Carro
                >>> Caminhão
                
                """);
        System.out.print("Digite aqui >>> ");
        var tipoDeVeiculoPesquisado = teclado.nextLine();

        String endereco;
        if (tipoDeVeiculoPesquisado.trim().toLowerCase().contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        } else if (tipoDeVeiculoPesquisado.trim().toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        List<DadosBase> marcaVeiculoList = converteDados.serializaLista(consumoAPI.obterDados(endereco), DadosBase.class);
        marcaVeiculoList.stream().sorted(Comparator.comparing(marcaVeiculo -> marcaVeiculo.nome().toLowerCase())).forEach(System.out::println);

        System.out.print("\nInforme o codigo da marca do veículo que deseja >>> ");
        var codigoMarcaVeiculoPesquisado = teclado.nextLine();
        endereco = endereco+"/"+codigoMarcaVeiculoPesquisado+"/modelos";

        var modeloVeiculoList = converteDados.serializaDados(consumoAPI.obterDados(endereco), ModeloVeiculo.class);
        modeloVeiculoList.modelos().stream().sorted(Comparator.comparing(modelo -> modelo.nome().toLowerCase())).forEach(System.out::println);

        System.out.print("\nInforme o codigo do modelo do veículo que deseja >>> ");
        var codigoModeloVeiculoPesquisado = teclado.nextLine();
        endereco = endereco+"/"+codigoModeloVeiculoPesquisado+"/anos";

        List<DadosBase> anosModeloVeiculoPesquisado = converteDados.serializaLista(consumoAPI.obterDados(endereco), DadosBase.class);

        List<Veiculo> veiculosPesquisados = new ArrayList<>();
        for(int i = 0; i < anosModeloVeiculoPesquisado.size(); i++) {
            String enderecoFinal = endereco+"/"+anosModeloVeiculoPesquisado.get(i).codigo();
            veiculosPesquisados.add(converteDados.serializaDados(consumoAPI.obterDados(enderecoFinal), Veiculo.class));
        }

        veiculosPesquisados.stream().sorted(Comparator.comparing(veiculo -> veiculo.anoModelo())).forEach(veiculo -> {
            System.out.println(veiculo);
            System.out.println();
        });
    }

}
