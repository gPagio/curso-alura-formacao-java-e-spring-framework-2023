package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository respositorio;

    public List<SerieDTO> obterTodasAsSeries (){
        return listSerieToListSerieDTO(respositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series(){
        return listSerieToListSerieDTO(respositorio.findTop5ByOrderByAvaliacaoDesc());

    }

    private List<SerieDTO> listSerieToListSerieDTO(List<Serie> listaSerie){
        return listaSerie.stream()
                .map(serie -> new SerieDTO(serie.getId()
                        , serie.getTitulo()
                        , serie.getTotalTemporadas()
                        , serie.getAvaliacao()
                        , serie.getGenero()
                        , serie.getAtores()
                        , serie.getPoster()
                        , serie.getSinopse())).collect(Collectors.toList());
    }

    private List<EpisodioDTO> listEpisodioToListEpisodioDTO(List<Episodio> episodios) {
        return episodios.stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterLancamento() {
        return listSerieToListSerieDTO(respositorio.encontrarSerieMaisRecentes());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serie = respositorio.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasAsTemporadas(Long id) {
        Optional<Serie> serie = respositorio.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return listEpisodioToListEpisodioDTO(s.getEpisodios());
        }
        return null;
    }


    public List<EpisodioDTO> obterTemporadaEspecifica(Long id, Long numero) {
        return listEpisodioToListEpisodioDTO(respositorio.obterEpisodiosPorTemporada(id, numero));
    }

    public List<SerieDTO> obterSeriesPorGenero(String nomeGenero) {
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        return listSerieToListSerieDTO(respositorio.findByGenero(categoria));
    }

    public List<EpisodioDTO> obterTop5EpisodiosPorSerie(Long idSerie) {
        return listEpisodioToListEpisodioDTO(respositorio.topEpisodiosPorSerie(idSerie));
    }
}
