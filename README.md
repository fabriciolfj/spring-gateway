# Spring gateway
- Projeto spring, como o próprio nome salienta, cujo objetivo é ser um gateway para suas aplicações.
- Além de fazer parte dos patterns de microservices, a adoção de um gateway traz inúmeros benefícios, entre eles:
  - fornece um ponto de entrada para o sistema
  - efetua o encaminhamento das chamadas para o serviço responsável, atravez das rotas
  - podemos centralizar nele algumas preocupações transversais, como segurança, monitoramento e resiliência.

## Rotas
- É identificada por um ID único, uma coleção de predicados dedicindo se devem seguir a rota, um URI para encaminhamento da solicitação se os predicados permitirem, e uma coleção de filtros aplicados antes ou depois de encaminhar a solicitação ao downstream.

## Predicates
- Corresponde a qualquer coisa da solicitação HTTP, incluindo path, host, headers, parâmetros de consulta, cookies e body.

## Filter
- Modifica a solicitação ou resposta HTTP antes ou depois de encaminhar a mesma para serviço downstream.

### Retry filter
- Podemos configurar o gateway para efetuar uma retentativa, diante falha, ao serviço downstream. Para isso:
  - configurar o tipo de erro/exceção que ocorrerá a retentativa.
  - o time entre as retentativas

## Circuit breaker
- Uma forma de mantermos nossos serviços resilientes, ou seja, diante a falhas mante-lo em funcionamento
- Este recurso vem incluso no spring cloud gateway, chamado circuit breaker.
- Tal conceito vem de componentes eletrícos, e seu uso é bem similar.
- Temos 3 estados: circuito aberto, semi aberto e fechado. Exemplo:
  - Quando um serviço está apresentando falha, o circuito é aberto e executa um outro recurso (fallback).
  - Enquanto o circuito estiver aberto, o recurso original não será chamado.
  - Tempos em tempos, este verifica se o serviço original está operante, caso positivo, o circuito é fechado, caso negativo, circuito manten-se aberto.
- Precisa de um bean Customizer dentro do gateway, para o circuitbreaker funcionar, alem das configurações no application.yml

```
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
```    
- Obs: o tempo limite configurado no customizer, terá precedência sobre o tempo limite de resposta definido netty timeout application.yml

### Combinando vários padrões de resiliência
- Quando se combina vários padrões de resiliência, a sequência fica o seguinte:
  - 1 TimeLimiter: tempo limite de resposta ao cliente.
  - 2 Filtro circuitBreaker
  - 3 Retry (retentativas)
