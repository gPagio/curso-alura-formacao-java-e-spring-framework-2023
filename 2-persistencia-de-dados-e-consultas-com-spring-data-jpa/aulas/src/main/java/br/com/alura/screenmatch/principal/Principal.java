package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio){
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por titulo
                    5 - Buscar série por ator
                    6 - Top 5 Series
                    7 - Buscar Series por Categoria
                    8 - Buscar Série com Numero Maximo de Temporadas e Avaliação Mínima
                    9 - Buscar Episódios por Trecho
                    10 - Buscar Top Episódios por Serie
                    11 - Buscar episódios a partir de uma data
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarSeriePorTotalTemporadasEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        repositorio.save(new Serie(dados));
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);

            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(dadosTemporada -> dadosTemporada.episodios()
                            .stream()
                            .map(dadosEpisodio -> new Episodio(dadosTemporada.numero(), dadosEpisodio)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

        try {
            serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

            if (serieBusca.isPresent()){
                System.out.println("Dados da série: " + serieBusca.get());
            } else {
                System.out.println("Série não encontrada!");
            }
        } catch (NonUniqueResultException | IncorrectResultSizeDataAccessException e){
            System.out.println("Informe um nome mais específico, pois foram encontradas mais de uma série!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator para busca");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();
        List<Serie> serieBuscada = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        if (!serieBuscada.isEmpty()){
            System.out.println("Séries em que " + nomeAtor.substring(0, 1).toUpperCase() + nomeAtor.substring(1) + " participou: ");
            serieBuscada.forEach(s -> System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("O ator não está em nenhuma série registrada!");
        }
    }

    private void buscarTop5Series(){
        List<Serie> top5Series = repositorio.findTop5ByOrderByAvaliacaoDesc();
        top5Series.forEach(s -> System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Deseja buscar séries de que categoria/gênero?");
        var nomeGenero = leitura.nextLine();

        try {
            Categoria categoria = Categoria.fromPortugues(nomeGenero);
            List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);
            System.out.println("Serie pela categoria " + categoria);
            seriePorCategoria.forEach(s -> System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void buscarSeriePorTotalTemporadasEAvaliacao(){
        System.out.println("Digite o número máximo de temporadas");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Digite a avaliação mínima");
        var avaliacaoMinima = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> seriesParaAssistir = repositorio.seriesPorTemporadaEAValiacao(totalTemporadas, avaliacaoMinima);
        if (!seriesParaAssistir.isEmpty()){
            System.out.println("Séries com até " + totalTemporadas + " temporadas e avaliação mínima de " + avaliacaoMinima);
            seriesParaAssistir.forEach(s -> System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Nenhuma série encontrada com os critérios informados");
        }
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Digite o nome do episódio para busca");
        var nomeTrechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(nomeTrechoEpisodio);
        if (!episodiosEncontrados.isEmpty()){
            episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n", e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
        } else {
            System.out.println("Nenhum episódio encontrado com o nome informado");
        }
    }

    private void buscarTopEpisodiosPorSerie(){
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);

            if (!topEpisodios.isEmpty()){
                topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %.2f\n", e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
            }
        }
    }

    private void buscarEpisodiosDepoisDeUmaData(){
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()){
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serieBusca.get(), anoLancamento);
            episodiosAno.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s Ano Lançamento Episódio %d\n", e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getDataLancamento().getYear()));
        } else {
            System.out.println("Nenhum episódio encontrado com as informações fornecidas!");
        }
    }
}