package nl.jimkaplan.foxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.jimkaplan.foxy.model.ChatRequest;
import nl.jimkaplan.foxy.model.ChatResponse;
import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.service.ProviderService;
import nl.jimkaplan.foxy.web.ApiResponse;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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
@Slf4j
public class ChatController {
    private final ProviderService providerService;
    private final RestTemplate restTemplate;
    private static final Marker CHAT_MARKER = MarkerFactory.getMarker("CHAT");

    @Operation(summary = "Create chat completion")
    @PostMapping("/chat/completions")
    public ResponseEntity<ApiResponse<?>> createChatCompletion(
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
            log.info("Successfully processed chat request | Status: {}", response.getStatusCode());
            ApiResponse<ChatResponse> apiResponse = ApiResponse.success(response.getBody());
            return ResponseEntity.status(response.getStatusCode()).body(apiResponse);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Log the exception for debugging
            log.error(CHAT_MARKER, "API Error: Status={}, Response={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            // Sanitize the exception message
            String sanitizedMessage = "An error occurred. " + e.getMessage();
            // Create ProblemDetail (Spring's standard error response)
            ProblemDetail problem = ProblemDetail.forStatus(e.getStatusCode());
            problem.setTitle("Chat API Error");
            problem.setDetail("Failed to process chat request. " + sanitizedMessage);

            ApiResponse<?> apiResponse = ApiResponse.error(problem);
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(apiResponse);
        }
    }
}
