package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

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
    List<Serie> series = new ArrayList<>();

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
//        dadosSeries.add(dados);
//        System.out.println(dados);

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
//        List<Serie> series = new ArrayList<>();
//        series = dadosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator para busca");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();
        Optional<List<Serie>> serieBuscada = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        if (serieBuscada.isPresent()){
            System.out.println("Séries em que " + nomeAtor.substring(0, 1).toUpperCase() + nomeAtor.substring(1) + " participou: ");
            serieBuscada.get().forEach(s -> System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao()));
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

        Optional<List<Serie>> seriesParaAssistir = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(totalTemporadas, avaliacaoMinima);
        if (seriesParaAssistir.isPresent()){
            System.out.println("Séries com até " + totalTemporadas + " temporadas e avaliação mínima de " + avaliacaoMinima);
            seriesParaAssistir.get().forEach(s -> System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Nenhuma série encontrada com os critérios informados");
        }
    }
}