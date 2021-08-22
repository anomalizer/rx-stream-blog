package net.anomalizer.j2se.rxblog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ImperativeApproach {
    private final String cityUpstreamPath;
    private final Gson gson;

    public ImperativeApproach(String queryURI) {
        this.cityUpstreamPath = queryURI;
        gson = new Gson();
    }

    public Map<Integer, City> fetchCity() throws Exception {
        List<City> cities = new ArrayList<>();
        String urlPath = UrlUtils.getUrlWithQueryParams(cityUpstreamPath, null);
        recursiveRead(cities, cityUpstreamPath);
        return cities.stream().collect(Collectors.toMap(City::getCityId, Function.identity()));
    }

    private void recursiveRead(List<City> cities, String urlPath) throws IOException {
        try (val client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(urlPath);
            request.setHeader("content-type", "application/json");
            try (val httpResponse = client.execute(request)) {
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String entityString = EntityUtils.toString(httpResponse.getEntity());
                    PaginatedResponse<City> siloResponse =
                            gson.fromJson(entityString, new TypeToken<PaginatedResponse<City>>(){}.getType());
                    cities.addAll(siloResponse.getResults());
                    if (siloResponse.getNext() != null) {
                        recursiveRead(cities, siloResponse.getNext());
                    } else {
                        log.info("Fetched:", cities.size());
                    }
                } else {
                    log.error("Failed to get Cities");
                }
            }
        }
    }
}
