package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.security.spec.ECPoint;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("TOP 10 Episodios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(p -> System.out.println("Filtro N/A " + p))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(p -> System.out.println("Ordenação " + p))
//                .limit(10)
//                .peek(p -> System.out.println("Limit " + p))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(p -> System.out.println("Mapeamento " + p))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.print("Digite um trecho do titulo do episódio >>> ");
//        var trechoTitulo = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream().filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase())).findFirst();
//
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada() + ", episódio: " + episodioBuscado.get().getNumeroEpisodio());
//        } else {
//            System.out.println("Episódio não encontrado!");
//        }
//
//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        System.out.print("Digite aqui >>> ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//        episodios.stream().filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca)).forEach(e -> System.out.println(
//                "Temporada: " + e.getTemporada() + ", episodio: " + e.getTitulo() + ", data de lançamento: " + e.getDataLancamento().format(formatador)
//        ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacaoEpisodio() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada
                        , Collectors.averagingDouble(Episodio::getAvaliacaoEpisodio)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream().filter(e -> e.getAvaliacaoEpisodio() > 0.0).collect(Collectors.summarizingDouble(Episodio::getAvaliacaoEpisodio));
        System.out.println();
        System.out.println("Estatísticas");
        System.out.println(est);
        System.out.println("Media: " + est.getAverage());
        System.out.println("Melhor Episodio: " + est.getMax());
        System.out.println("Pior Episodio: " + est.getMin());
        System.out.println("Quantidade Episodios Avaliados: " + est.getCount());
    }

}
