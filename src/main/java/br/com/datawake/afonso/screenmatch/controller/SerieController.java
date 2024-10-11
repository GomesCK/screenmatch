package br.com.datawake.afonso.screenmatch.controller;

import br.com.datawake.afonso.screenmatch.model.Serie;
import br.com.datawake.afonso.screenmatch.repository.SerieRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SerieController {

    private SerieRepository repositorio;

    @GetMapping("/series")
    public List<Serie> obterSeries() {
        return repositorio.findAll();
    }
}
