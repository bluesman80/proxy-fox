package nl.jimkaplan.foxy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.client.RestClientException;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "Chat completion endpoints")
public class ChatController {
    private final ProviderService providerService;
    private final RestTemplate restTemplate;
    private static final Marker CHAT_MARKER = MarkerFactory.getMarker("CHAT");

    @Operation(
        summary = "Create chat completion",
        description = "Forwards chat completion request to selected AI provider"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful chat completion"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/chat/completions")
    public ResponseEntity<ApiResponse<?>> createChatCompletion(
            @RequestBody @Valid ChatRequest chatRequest,
            @RequestHeader(value = "X-Organization", required = false) 
            @Parameter(description = "Organization identifier") String organization,
            @RequestHeader(value = "X-Project", required = false)
            @Parameter(description = "Project identifier") String project) {

        List<Provider> providers = providerService.getAvailableProviders(organization, project);
        if (providers.isEmpty()) {
            ProblemDetail problem = ProblemDetail.forStatus(503);
            problem.setTitle("No Providers Available");
            problem.setDetail("No AI providers are currently configured");
            return ResponseEntity.status(503)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(ApiResponse.error(problem));
        }

        boolean isModelSet = true;
        if (chatRequest.getModel() == null || chatRequest.getModel().isEmpty()) {
            isModelSet = false;
        }

        Exception lastException = null;

        for (Provider provider : providers) {
            try {
                if (!isModelSet) {
                    chatRequest.setModel(provider.getDefaultModel());
                }
                return attemptChatCompletion(chatRequest, provider);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                lastException = e;
                log.error(CHAT_MARKER, "Provider {} failed: Status={}, Response={}", 
                    provider.getName(), e.getStatusCode(), e.getResponseBodyAsString(), e);
                continue; // Try next provider
            } catch (RestClientException e) {
                lastException = e;
                log.error(CHAT_MARKER, "Provider {} failed: {}", provider.getName(), e.getMessage(), e);
                continue; // Try next provider
            }
        }

        // If we get here, all providers failed
        log.error(CHAT_MARKER, "All providers failed to process request", lastException);
        ProblemDetail problem = ProblemDetail.forStatus(503);
        problem.setTitle("Service Unavailable");
        problem.setDetail("All available AI providers failed to process the request");
        
        return ResponseEntity.status(503)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(ApiResponse.error(problem));
    }

    private ResponseEntity<ApiResponse<?>> attemptChatCompletion(ChatRequest chatRequest, Provider provider) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(provider.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(chatRequest, headers);
        String url = provider.getUrl() + "chat/completions";

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                url,
                entity,
                ChatResponse.class
        );
        
        log.info("Successfully processed chat request with provider {} | Status: {}", 
            provider.getName(), response.getStatusCode());
        log.debug("Request: {}", entity.getBody());

        ApiResponse<ChatResponse> apiResponse = ApiResponse.success(response.getBody());
        return ResponseEntity.status(response.getStatusCode()).body(apiResponse);
    }
}
