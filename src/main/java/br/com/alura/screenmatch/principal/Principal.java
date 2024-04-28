package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f1cb7b78";

    public void exibeMenu(){
        System.out.print("Digite o nome da série para busca >>> ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.trim().replace(" ", "+") + API_KEY);

        DadosSerie dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);
        System.out.println();

		System.out.println("Temporadas:");
		List<DadosTemporada> temporadas = new ArrayList<>();
		for (int i = 1; i <= dadosSerie.totalTemporadas(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.trim().replace(" ", "+") + "&season=" + i + API_KEY);
                temporadas.add(converteDados.obterDados(json, DadosTemporada.class));
		}
		temporadas.forEach(System.out::println);
		System.out.println();

//        for (int i = 0; i < dadosSerie.totalTemporadas(); i++){
//            int numeroTemporada = i+1;
//            System.out.println("Temporada " + numeroTemporada + ":");
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++){
//                int numeroEpisodio = j+1;
//                System.out.println("Episódio " + numeroEpisodio + ": " + episodiosTemporada.get(j).titulo());
//            }
//            System.out.println();
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }

}
