package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository respositorio;

    public List<SerieDTO> obterTodasAsSeries (){
        return respositorio.findAll().stream()
                .map(serie -> new SerieDTO(serie.getId()
                        , serie.getTitulo()
                        , serie.getTotalTemporadas()
                        , serie.getAvaliacao()
                        , serie.getGenero()
                        , serie.getAtores()
                        , serie.getPoster()
                        , serie.getSinopse())).collect(Collectors.toList());
    }
}
