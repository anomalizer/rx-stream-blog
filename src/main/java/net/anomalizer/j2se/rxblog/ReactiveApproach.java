package net.anomalizer.j2se.rxblog;

import jdk.internal.joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@Slf4j
public class ReactiveApproach {
    private final String cityUpstreamPath;

    public ReactiveApproach(String queryURI) {
        this.cityUpstreamPath = queryURI;
    }

    public Mono<Map<Integer, City>> fetchCityReactive() {
        String urlPath;
        try {
            urlPath = UrlUtils.getUrlWithQueryParams(cityUpstreamPath, null);
        } catch (Exception e) {
            return Mono.error(e);
        }

        Flux<City> cities = recursiveRead2(urlPath);
        return cities.collectMap(City::getCityId, Function.identity());
    }

    private static Flux<City> recursiveRead2(String urlPath) {
        Mono<PaginatedResponse<City>> siloResponse = WebClient.create().get()
                .uri(urlPath)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PaginatedResponse<City>>() {
                });

        siloResponse.doOnError((e) -> log.error("Failed to get cities", e));

        Flux<City> currentPage = siloResponse.map(PaginatedResponse::getResults).flatMapMany(Flux::fromIterable);
        currentPage.count().subscribe(c -> log.info("Fetched {} cities", c));

        Flux<City> nextPage = siloResponse.map(PaginatedResponse::getNext)
                .filter(x -> !Strings.isNullOrEmpty(x))
                .flatMapMany(ReactiveApproach::recursiveRead2);

        return Flux.concat(currentPage, nextPage);
    }

}
