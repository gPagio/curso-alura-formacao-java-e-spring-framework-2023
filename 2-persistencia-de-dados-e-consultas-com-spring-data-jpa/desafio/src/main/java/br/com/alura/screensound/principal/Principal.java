package br.com.alura.screensound.principal;

import br.com.alura.screensound.model.Artista;
import br.com.alura.screensound.model.Musica;
import br.com.alura.screensound.model.TipoArtista;
import br.com.alura.screensound.repository.ArtistaRepository;
import br.com.alura.screensound.service.ConsultaChatGPT;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private final ArtistaRepository repositorio;

    public Principal(ArtistaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao!= 0) {
            var menu = """
                    *** Screen Sound Músicas ***                    
                                        
                    1- Cadastrar artistas
                    2- Cadastrar músicas
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista
                                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 0:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void cadastrarArtistas() {
        var cadastrarNovo = "S";

        while (cadastrarNovo.equalsIgnoreCase("S")){
            System.out.println("Informe o nome desse artista:");
            var nome = leitura.nextLine();
            System.out.println("Informe o tipo desse artista: (solo, dupla, banda)");
            TipoArtista tipoArtista = TipoArtista.valueOf(leitura.nextLine().toUpperCase());
            Artista artista = new Artista(nome, tipoArtista);
            repositorio.save(artista);
            System.out.println("Cadastrar novo artista? (S/N)");
            cadastrarNovo = leitura.nextLine();
        }
    }

    private void cadastrarMusicas() {
        System.out.println("Cadastrar música de qual artista?");
        var nome = leitura.nextLine();
        Optional<Artista> artista = repositorio.findByNomeContainingIgnoreCase(nome);

        if (artista.isPresent()){
            System.out.println("Digite o nome da música");
            var nomeMusica = leitura.nextLine();
            Musica musica = new Musica(nomeMusica);
            musica.setArtista(artista.get());
            artista.get().getMusicas().add(musica);
            repositorio.save(artista.get());
        } else {
            System.out.println("Artista não encontrado!");
        }
    }

    private void listarMusicas() {
        List<Artista> artistaList = repositorio.findAll();
        artistaList.forEach(artista -> artista.getMusicas().forEach(System.out::println));
    }

    private void buscarMusicasPorArtista() {
        System.out.println("Digite o nome do artista");
        var nomeArtista = leitura.nextLine();
        Optional<Artista> artista = repositorio.findByNomeContainingIgnoreCase(nomeArtista);

        if (artista.isPresent()){
            Artista a = artista.get();
            List<Musica> musicaDoArtista = repositorio.buscaMusicaPorArtista(a.getNome());
            System.out.println("Músicas de " + a.getNome());
            musicaDoArtista.forEach(System.out::println);
        } else {
            System.out.println("Artista não cadastrado!");
        }
    }

    private void pesquisarDadosDoArtista() {
        var consultarNovamente = "S";

        while (consultarNovamente.equalsIgnoreCase("S")){
            System.out.println("Pesquisar dados sobre qual artista?");
            var nomeArtista = leitura.nextLine();

            String resposta;

            try{
                resposta = ConsultaChatGPT.obterInformacao(nomeArtista).trim();
                System.out.println(resposta);
            } catch (Exception e){
                System.out.println("\nA API apresentou o seguinte erro:\n" + e.getMessage());
            }

            System.out.println("\nDeseja consultar outro artista? (S/N)");
            consultarNovamente = leitura.nextLine();
        }
    }
}

