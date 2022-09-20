package com.search.infrastructure.client.builder;

import com.search.domain.model.location.Locations;
import com.search.domain.vo.Keyword;
import com.search.infrastructure.client.builder.kakao.KakaoApiInfo;
import com.search.infrastructure.client.builder.kakao.KakaoClientMonoBuilder;
import com.search.infrastructure.client.builder.naver.NaverApiInfo;
import com.search.infrastructure.client.builder.naver.NaverClientMonoBuilder;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

/**
 *
 * @author o118014_D
 * @since 2022-09-20
 */

class ClientMonoBuilderTest {

    @Test
    void kakaoTest() {
        KakaoApiInfo kakaoApiInfo = kakaoApiInfo();
        ClientMonoBuilder clientMonoBuilder = new KakaoClientMonoBuilder(kakaoApiInfo);

        Mono<Locations> result = clientMonoBuilder.buildFor(new Keyword("곱창"));

        Locations block = result.block();
        Assertions.assertThat(block).isNotNull();
    }

    @Test
    void naverTest() {
        NaverApiInfo naverApiInfo = naverApiInfo();
        ClientMonoBuilder clientMonoBuilder = new NaverClientMonoBuilder(naverApiInfo);

        Mono<Locations> result = clientMonoBuilder.buildFor(new Keyword("곱창"));

        Locations block = result.block();
        Assertions.assertThat(block).isNotNull();
    }

    private KakaoApiInfo kakaoApiInfo() {
        return KakaoApiInfo.builder()
                .host("https://dapi.kakao.com")
                .path("/v2/local/search/keyword.json")
                .apiKey("50d28f6653163fb835fe5931f9cf3ed3")
                .build();
    }

    private NaverApiInfo naverApiInfo() {
        return NaverApiInfo.builder()
                .host("https://openapi.naver.com")
                .path("/v1/search/local.json")
                .clientId("_8o4EDT1hTGUY4iPKU90")
                .clientSecret("W8zCY43K_R")
                .displayCount("5")
                .build();
    }
}