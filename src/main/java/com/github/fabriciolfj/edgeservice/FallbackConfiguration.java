package com.github.fabriciolfj.edgeservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Configuration
public class FallbackConfiguration {

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(
                RequestPredicates.GET("/catalog-fallback"),
                this::handleGetFallback)
                .andRoute(RequestPredicates.POST("/catalog-fallback"),
                        this::handlePostFallback);
    }

    public Mono<ServerResponse> handleGetFallback(final ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("Fallback"), String.class);
    }

    public Mono<ServerResponse> handlePostFallback(final ServerRequest request) {
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
