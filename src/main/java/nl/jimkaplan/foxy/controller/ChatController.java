package nl.jimkaplan.foxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nl.jimkaplan.foxy.model.ChatRequest;
import nl.jimkaplan.foxy.model.ChatResponse;
import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.service.ProviderService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChatController {
    private final ProviderService providerService;
    private final RestTemplate restTemplate;

    @Operation(summary = "Create chat completion")
    @PostMapping("/chat/completions")
    public ResponseEntity<ChatResponse> createChatCompletion(
            @RequestBody ChatRequest chatRequest,
            @RequestHeader(value = "X-Organization", required = false) String organization,
            @RequestHeader(value = "X-Project", required = false) String project) {

        Provider provider = providerService.selectProvider(organization, project);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(provider.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(chatRequest, headers);
        String url = provider.getUrl() + "chat/completions";

        try {
            ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                url,
                entity,
                ChatResponse.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }
}
