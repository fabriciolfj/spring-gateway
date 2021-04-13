package com.github.fabriciolfj.edgeservice;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(CircuitBreakerConfig
                .custom()
                .slidingWindowSize(20) //numero de janelas para registar as chamdas no estado fechado
                .permittedNumberOfCallsInHalfOpenState(5) //quantidade e chamadas no estado semi aberto
                .failureRateThreshold(50) //percentual de falhas sobre as janelas, para abrir o circuito
                .waitDurationInOpenState(Duration.ofSeconds(6)) //tempo de espera antes de passar para o semiaberto
                .build()
        ).timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(TIMEOUT)
                        .build())
        .build());
    }
}
