package br.com.datawake.afonso.screenmatch.principal;

import br.com.datawake.afonso.screenmatch.model.DadosEpisodios;
import br.com.datawake.afonso.screenmatch.model.DadosSerie;
import br.com.datawake.afonso.screenmatch.model.DadosTemporada;
import br.com.datawake.afonso.screenmatch.model.Episodio;
import br.com.datawake.afonso.screenmatch.service.ConsumoApi;
import br.com.datawake.afonso.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner sc = new Scanner(System.in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    public void exibeMenu(){
        System.out.println("Digite o nome da série que deseja buscar:");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(
                ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);
        List<DadosTemporada> temporadas = new ArrayList<>();

        for(int i = 1; i <= dados.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//        for(int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodios> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        List<DadosEpisodios> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\n Top 5 episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemp(), d))
                ).collect(Collectors.toList());
        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do título do episódio");
        var trechoTitulo = sc.nextLine();
        Optional<Episodio> episodioBusca = episodios.stream()
                        .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                                .findFirst();
        if(episodioBusca.isPresent()){
            System.out.println("Episodio encontrado!!");
            System.out.println("Temporada: " + episodioBusca.get().getTemporada());
        } else{
            System.out.println("Não foi encontrado nenhum episodio!!");
        }

        System.out.println("A partir de que ano gostaria de filtrar??");
        var ano = sc.nextInt();
        sc.nextLine();

        LocalDate localDate = LocalDate.of(ano,1,1);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(localDate))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episodios: " + e.getTitulo() +
                                " Data lançamento: " + e.getDataLancamento().format(formato)
                ));

        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Quantidade: " + est.getCount());
        System.out.println("Mínimo: " + est.getMin());
        System.out.println("Máximo: " + est.getMax());
    }
}
