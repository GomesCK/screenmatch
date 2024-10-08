package br.com.datawake.afonso.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    public Episodio(Integer numeroTemporada, DadosEpisodios dadosEpisodios) {
        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodios.titulo();
        this.numeroEpisodio = dadosEpisodios.numeroEp();
        try {
            this.avaliacao = Double.valueOf(dadosEpisodios.avaliacao());
        }catch (NumberFormatException ex){
            this.avaliacao = 0.0;
        }
        try {
            this.dataLancamento = LocalDate.parse(dadosEpisodios.dataLancamento());
        }catch (DateTimeParseException ex){
            this.dataLancamento = null;
        }
    }

    public Integer getTemporada() {
        return temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public String toString() {
        return "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", avaliacao=" + avaliacao +
                ", dataLancamento=" + dataLancamento;
    }
}
