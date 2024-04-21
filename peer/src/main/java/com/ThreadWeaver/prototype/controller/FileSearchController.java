package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.dto.SearchDTO;
import com.ThreadWeaver.prototype.model.SizeRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Controller
public class FileSearchController {

    private final WebClient webClient;

    public FileSearchController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080").build();
    }


    @PostMapping("/search")
    public String searchFiles(@RequestParam("query") String query, @RequestParam("searchType") String searchType, @RequestParam(value = "minSize", required = false) Long minSize, @RequestParam(value = "maxSize", required = false) Long maxSize, Model model) {
        System.out.println(searchType);
        if (searchType.equals("size")) {
            Mono<ResponseEntity<List<SearchDTO>>> response = webClient.post()
                    .uri("/api/search/size")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new SizeRequest(minSize, maxSize)))
                    .retrieve()
                    .toEntityList(SearchDTO.class);

            response.subscribe(responseEntity -> {
                System.out.println("Response status: " + responseEntity.getStatusCode());
                System.out.println("Response body: " + responseEntity.getBody());
            });
        }
        else if (searchType.equals("keyword")){
            Mono<ResponseEntity<List<SearchDTO>>> response = webClient.post()
                    .uri("/api/search/keyword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(query))
                    .retrieve()
                    .toEntityList(SearchDTO.class);

            response.subscribe(responseEntity -> {
                System.out.println("Response status: " + responseEntity.getStatusCode());
                System.out.println("Response body: " + responseEntity.getBody());
            });
        }

        else if (searchType.equals("fileType")) {
            List<String> types = Arrays.asList(query.split(","));
            System.out.println(types);
            Mono<ResponseEntity<List<SearchDTO>>> response = webClient.post()
                    .uri("/api/search/type")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(types))
                    .retrieve()
                    .toEntityList(SearchDTO.class);

            response.subscribe(responseEntity -> {
                System.out.println("Response status: " + responseEntity.getStatusCode());
                System.out.println("Response body: " + responseEntity.getBody());
            });
        }

        model.addAttribute("message", "File search successfully: ");
        return "redirect:/";
    }
}
