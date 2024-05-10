package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SerieController {

    @Autowired
    private SerieRepository respositorio;

    @GetMapping("/series")
    public List<SerieDTO> obterSeries(){

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
