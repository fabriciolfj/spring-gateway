package com.github.fabriciolfj.edgeservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class SecurityConfig {

    /*
    * Armazenar o token e o token de atualizacao no redis
    * */
    @Bean
    public ServerOAuth2AuthorizedClientRepository auth2AuthorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http, final ReactiveClientRegistrationRepository repository) {
        return http
                .authorizeExchange(e ->
                        e.pathMatchers("/","/.css", "/.js", "/favicon.ico").permitAll()
                                .anyExchange().authenticated())
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(Customizer.withDefaults())
                .logout(logoutSpec -> logoutSpec.logoutSuccessHandler(oidcLogoutSuccessHandler(repository)))
                .build();
    }

    @Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            String key = CsrfToken.class.getName();
            Mono<CsrfToken> csrfToken = exchange.getAttributes().containsKey(key) ? exchange.getAttribute(key) : Mono.empty();
            return Objects.requireNonNull(csrfToken)
                    .doOnSuccess(token -> {})
                    .then(chain.filter(exchange));
        };
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }
}
