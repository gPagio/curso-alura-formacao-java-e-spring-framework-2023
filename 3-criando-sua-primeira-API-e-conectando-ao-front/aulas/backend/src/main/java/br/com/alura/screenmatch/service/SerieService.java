package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SerieDTO;
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

    public List<SerieDTO> obterLancamento() {
        return listSerieToListSerieDTO(respositorio.findTop5ByOrderByEpisodiosDataLancamentoDesc());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serie = respositorio.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }
}
