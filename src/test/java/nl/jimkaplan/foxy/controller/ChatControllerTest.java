package nl.jimkaplan.foxy.controller;

import nl.jimkaplan.foxy.model.ChatMessage;
import nl.jimkaplan.foxy.model.ChatRequest;
import nl.jimkaplan.foxy.model.ChatResponse;
import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.service.ProviderService;
import nl.jimkaplan.foxy.web.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ProviderService providerService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ChatController chatController;

    private Provider mockProvider;
    private ChatRequest chatRequest;
    private ChatResponse chatResponse;

    @BeforeEach
    void setUp() {
        mockProvider = new Provider();
        mockProvider.setName("TestProvider");
        mockProvider.setUrl("http://test-provider.com/");
        mockProvider.setApiKey("test-api-key");
        mockProvider.setDefaultModel("gpt-3.5-turbo");

        chatRequest = new ChatRequest(List.of(new ChatMessage("user", "Hello!")));
        chatRequest.setModel("gpt-3.5-turbo");

        chatResponse = new ChatResponse();
        // Set any necessary fields in chatResponse
    }

    @Test
    void whenNoProvidersAvailable_thenReturn503() {
        when(providerService.getAvailableProviders(any(), any()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<?>> response = chatController.createChatCompletion(
                chatRequest, null, null);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
    }

    @Test
    void whenProviderSucceeds_thenReturnSuccess() {
        when(providerService.getAvailableProviders(any(), any()))
                .thenReturn(Collections.singletonList(mockProvider));
        
        when(restTemplate.postForEntity(
                anyString(),
                any(),
                eq(ChatResponse.class)))
                .thenReturn(ResponseEntity.ok(chatResponse));

        ResponseEntity<ApiResponse<?>> response = chatController.createChatCompletion(
                chatRequest, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void whenFirstProviderFailsButSecondSucceeds_thenReturnSuccess() {
        Provider secondProvider = new Provider();
        secondProvider.setName("SecondProvider");
        secondProvider.setUrl("http://second-provider.com/");
        secondProvider.setApiKey("second-api-key");
        secondProvider.setDefaultModel("gpt-4");

        when(providerService.getAvailableProviders(any(), any()))
                .thenReturn(Arrays.asList(mockProvider, secondProvider));

        when(restTemplate.postForEntity(
                eq(mockProvider.getUrl() + "chat/completions"),
                any(),
                eq(ChatResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        when(restTemplate.postForEntity(
                eq(secondProvider.getUrl() + "chat/completions"),
                any(),
                eq(ChatResponse.class)))
                .thenReturn(ResponseEntity.ok(chatResponse));

        ResponseEntity<ApiResponse<?>> response = chatController.createChatCompletion(
                chatRequest, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void whenAllProvidersFail_thenReturn503() {
        when(providerService.getAvailableProviders(any(), any()))
                .thenReturn(Collections.singletonList(mockProvider));

        when(restTemplate.postForEntity(
                anyString(),
                any(),
                eq(ChatResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<ApiResponse<?>> response = chatController.createChatCompletion(
                chatRequest, null, null);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, response.getHeaders().getContentType());
    }

    @Test
    void whenModelNotSpecified_thenUseProviderDefaultModel() {
        chatRequest.setModel(null);

        when(providerService.getAvailableProviders(any(), any()))
                .thenReturn(Collections.singletonList(mockProvider));
        
        when(restTemplate.postForEntity(
                anyString(),
                any(),
                eq(ChatResponse.class)))
                .thenReturn(ResponseEntity.ok(chatResponse));

        ResponseEntity<ApiResponse<?>> response = chatController.createChatCompletion(
                chatRequest, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProvider.getDefaultModel(), chatRequest.getModel());
    }
} 