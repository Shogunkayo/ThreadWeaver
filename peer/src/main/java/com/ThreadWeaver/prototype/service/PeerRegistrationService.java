package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.PeerRegistrationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PeerRegistrationService {

    private final WebClient webClient;

    public PeerRegistrationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080/").build();
    }

    public Mono<String> registerWithServer(PeerRegistrationRequest request) {
        return webClient.post()
                .uri("/api/peers/register")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(String.class);
    }
}