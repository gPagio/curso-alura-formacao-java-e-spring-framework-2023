package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.MarcaCarro;
import br.com.alura.tabelafipe.model.ModeloCarro;
import br.com.alura.tabelafipe.service.ConsumoAPI;
import br.com.alura.tabelafipe.service.ConverteDados;

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
                Escolha um tipo de carro para buscar
                
                >>> Moto
                >>> Carro
                >>> Caminhão
                
                """);
        System.out.print("Digite aqui >>> ");
        var tipoDeCarroPesquisado = teclado.nextLine();
        System.out.println("Escolheu " + tipoDeCarroPesquisado);

        String endereco;

        if (tipoDeCarroPesquisado.trim().toLowerCase().contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        } else if (tipoDeCarroPesquisado.trim().toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        List<MarcaCarro> marcaCarroList = converteDados.serializaLista(consumoAPI.obterDados(endereco), MarcaCarro.class);
        marcaCarroList.stream().sorted(Comparator.comparing(marcaCarro -> marcaCarro.nomeMarca().toLowerCase())).forEach(System.out::println);

        System.out.println();
        System.out.print("Informe a marca do carro que deseja >>> ");
        var marcaCarroPesquisado = teclado.nextLine();
        var codigoMarcaCarroPesquisado = marcaCarroList.stream().filter(marcaCarro -> marcaCarro.nomeMarca().toLowerCase().equals(marcaCarroPesquisado.toLowerCase())).findFirst().get().codigoMarca();
        endereco = endereco+"/"+codigoMarcaCarroPesquisado+"/modelos";

        System.out.println(endereco);

        List<ModeloCarro> modeloCarroList = converteDados.serializaLista(consumoAPI.obterDados(endereco), ModeloCarro.class);
        modeloCarroList.stream().sorted(Comparator.comparing(modeloCarro -> modeloCarro.nomeModelo().toLowerCase())).forEach(System.out::println);
    }

}
