package com.pm.apigateway.filter;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationGatewayFilterFactory extends
    AbstractGatewayFilterFactory<Object> {

  private final WebClient webClient;

  public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
      @Value("${auth.service.url}") String authServiceUrl) {
    this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
  }

  @Override
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      String authHeader =
          exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      if(authHeader == null || !authHeader.startsWith("Bearer ")) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }

      return webClient.get()
          .uri("/validate")
          .header(HttpHeaders.AUTHORIZATION, authHeader)
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
          .flatMap(claims -> {
            var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                  headers.remove("X-User-Id");
                  headers.remove("X-User-Email");
                  headers.remove("X-User-Role");
                  headers.set("X-User-Id", claims.get("userId"));
                  headers.set("X-User-Email", claims.get("email"));
                  headers.set("X-User-Role", claims.get("role"));
                })
                .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
          })
          .onErrorResume(WebClientResponseException.Unauthorized.class, ex -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
          });
    };
  }
}
