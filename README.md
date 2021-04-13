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
