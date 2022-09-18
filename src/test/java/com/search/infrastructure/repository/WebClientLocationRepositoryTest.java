package com.search.infrastructure.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class WebClientLocationRepositoryTest {

    @Test
    @DisplayName("카카오 API 호출 테스트")
    void 카카오_API_호출_테스트() {
        /*
        {
	"lastBuildDate":"Sun, 18 Sep 2022 22:56:20 +0900",
	"total":1,
	"start":1,
	"display":1,
	"items":[
		{
			"title":"청어람 망원점",
			"link":"",
			"category":"한식>곱창,막창,양",
			"description":"",
			"telephone":"",
			"address":"서울특별시 마포구 망원동 482-3",
			"roadAddress":"서울특별시 마포구 망원로 97",
			"mapx":"303671",
			"mapy":"551215"
		}
	]
}
         */
        LocationGetQuery query1 = makeKakaoQueryFor("청어람 망원점");
        LocationGetQuery query2 = makeKakaoQueryFor("갈비");

        Mono<String> searchResultMono1 = clientRequest(query1);
        Mono<String> searchResultMono2 = clientRequest(query2);
        List<Mono<?>> resultList = Arrays.asList(searchResultMono1, searchResultMono2);
        Object[] response = Mono.zip(resultList, result -> result).block();
        System.out.println(response);
        assertThat(response).hasSize(resultList.size()).isNotNull();
    }

    @Test
    void 네이버_API_호출_테스트() {
        LocationGetQuery query1 = makeNaverQueryFor("곱창");
        LocationGetQuery query2 = makeNaverQueryFor("갈비");

        Mono<String> searchResultMono1 = clientRequest(query1);
        Mono<String> searchResultMono2 = clientRequest(query2);
        List<Mono<?>> resultList = Arrays.asList(searchResultMono1, searchResultMono2);
        Object[] response = Mono.zip(resultList, result -> result).block();
        System.out.println(response);
        assertThat(Mono.zip(resultList, result -> result).block())
                .hasSize(resultList.size())
                .isNotNull();
    }

    private Mono<String> clientRequest(LocationGetQuery locationGetQuery) {
        return WebClient.builder()
                .baseUrl(locationGetQuery.getHost()).build().get()
                .uri(uriBuilder -> uriBuilder.path(locationGetQuery.getPath())
                        .queryParam("query", locationGetQuery.getQuery()).build())
                .headers(eachHeaders -> locationGetQuery.getHeaders().forEach(eachHeaders::set))
                .exchangeToMono(response -> response.bodyToMono(String.class));
    }

    private LocationGetQuery makeKakaoQueryFor(String query) {
        String apiKey = "50d28f6653163fb835fe5931f9cf3ed3";
        String host = "https://dapi.kakao.com";
        String auth = "KakaoAK " + apiKey;
        String path = "/v2/local/search/keyword.json";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", auth);

        return new LocationGetQuery.Builder().host(host).path(path).headers(headers).query(query).build();
    }

    private LocationGetQuery makeNaverQueryFor(String query) {
        String host = "https://openapi.naver.com";
        String path = "/v1/search/local.json";
        String clientId = "_8o4EDT1hTGUY4iPKU90";
        String clientSecret = "W8zCY43K_R";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Naver-Client-Id", clientId);
        headers.put("X-Naver-Client-Secret", clientSecret);

        return new LocationGetQuery.Builder().host(host).path(path).headers(headers).query(query).build();
    }
}

class LocationGetQuery {
    private String host;
    private String path;
    private Map<String, String> headers;
    private String query;

    private LocationGetQuery() {
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getQuery() {
        return query;
    }

    private LocationGetQuery(Builder builder) {
        this.host = builder.host;
        this.headers = builder.headers;
        this.query = builder.query;
        this.path = builder.path;
    }

    public static class Builder {
        private String host;
        private Map<String, String> headers;
        private String query;
        private String path;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public LocationGetQuery build() {
            return new LocationGetQuery(this);
        }
    }
}