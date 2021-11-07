package com.github.fabriciolfj.edgeservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final String ACTUATOR = "/actuator/**";

    /*
    * Armazenar o token e o token de atualizacao no redis
    * */
    @Bean
    public ServerOAuth2AuthorizedClientRepository auth2AuthorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.
                exceptionHandling()
                .authenticationEntryPoint((serverWebExchange, e) ->
                        Mono.fromRunnable(() ->
                                serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
                        )
                ).accessDeniedHandler((serverWebExchange, e) -> Mono.fromRunnable(() ->
                serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
        )).and()
                ///.redirectToHttps().and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeExchange()
                .pathMatchers(ACTUATOR, "/swagger-ui.html", "/webjars/**",
                        "/swagger-resources/**", "/v2/api-docs/**", "/.well-known/**", "/", "/favicon.ico", "/swagger-ui/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

    /*
    * essa forma o gateway faz o tramite de pegar o token com base no session do front
    * */
    /*@Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http, final ReactiveClientRegistrationRepository repository) {
        return http
                .authorizeExchange(e ->
                        e.pathMatchers("/","/.css", "/.js", "/favicon.ico").permitAll()
                                .anyExchange().authenticated())
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))) //para nao retirecionar a pagina de login
                .oauth2Login(Customizer.withDefaults())
                .csrf().disable()
                .logout(logoutSpec -> logoutSpec.logoutSuccessHandler(oidcLogoutSuccessHandler(repository)))
                .build();
    }*/

    /*@Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            String key = CsrfToken.class.getName();
            Mono<CsrfToken> csrfToken = exchange.getAttributes().containsKey(key) ? exchange.getAttribute(key) : Mono.empty();
            return Objects.requireNonNull(csrfToken)
                    .doOnSuccess(token -> {})
                    .then(chain.filter(exchange));
        };
    }*/

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }
}
