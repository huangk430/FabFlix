package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.result.ResultMap;
import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.data.Posts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Component
public class AuthFilter implements GatewayFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);
    private Integer i = 0;

    private final GatewayServiceConfig config;
    private final WebClient            webClient;

    @Autowired
    public AuthFilter(GatewayServiceConfig config)
    {
        this.config = config;
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        Optional<String> accessToken = getAccessTokenFromHeader(exchange);

        if (!accessToken.isPresent()) {
            return setToFail(exchange);
        }

        authenticate(accessToken.get())
                .flatMap(result -> result.code() == 1040 ? chain.filter(exchange) : setToFail(exchange));

        return chain.filter(exchange);
    }

    private Mono<Void> setToFail(ServerWebExchange exchange)
    {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Takes in a accessToken token and creates Mono chain that calls the idm and maps the value to
     * a Result
     *
     * @param accessToken a encodedJWT
     * @return a Mono that returns a Result
     */
    private Mono<Result> authenticate(String accessToken)
    {
        Posts posts = new Posts()
                .setAccessToken(accessToken);
        return WebClient.create().post()
                .uri(config.getIdm() + "/authenticate")
                .bodyValue(posts)
                .retrieve()
                .bodyToMono(POJO.class)
                .map(response -> ResultMap.fromCode(response.getResult().getCode()));
    }

    private Optional<String> getAccessTokenFromHeader(ServerWebExchange exchange)
    {
        if (exchange.getRequest().getHeaders().get("Authorization") == null) {
            return Optional.empty();
        } else if (!exchange.getRequest().getHeaders().get("Authorization").get(0).contains("Bearer")) {
            return Optional.empty();
        }

        return Optional.ofNullable(Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization")).get(0).split(" ")[1]);
    }
}