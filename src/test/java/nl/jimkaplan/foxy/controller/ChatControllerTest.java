package nl.jimkaplan.foxy.controller;

import nl.jimkaplan.foxy.model.ChatMessage;
import nl.jimkaplan.foxy.model.ChatRequest;
import nl.jimkaplan.foxy.model.ChatResponse;
import nl.jimkaplan.foxy.model.Provider;
import nl.jimkaplan.foxy.service.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChatControllerTest {

    @Autowired
    private ChatController chatController;

    @MockitoBean
    private ProviderService providerService;

    @MockitoBean
    private RestTemplate restTemplate;

    private Provider provider;
    private ChatRequest chatRequest;
    private ChatResponse chatResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        provider = new Provider();
        provider.setName("openai");
        provider.setUrl("https://api.openai.com/v1/");
        provider.setApiKey("test-api-key");
        provider.setUsageFlag(true);
        provider.setPriority(1);

        chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setMessages(List.of(new ChatMessage("user", "Hello!")));

        chatResponse = new ChatResponse();
        chatResponse.setId("chatcmpl-123");
        chatResponse.setObject("chat.completion");
        chatResponse.setCreated(1677652288L);
        chatResponse.setModel("gpt-3.5-turbo");
    }

    @Test
    void testCreateChatCompletion_Success() {
        // Arrange
        when(providerService.selectProvider(any(), any())).thenReturn(provider);
        when(restTemplate.postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(ChatResponse.class)
        )).thenReturn(new ResponseEntity<>(chatResponse, HttpStatus.OK));

        // Act
        ResponseEntity<ChatResponse> response = chatController.createChatCompletion(
                chatRequest, "Org1", "Project1"
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("chatcmpl-123", response.getBody().getId());
        assertEquals("gpt-3.5-turbo", response.getBody().getModel());

        // Verify mocks
        verify(providerService, times(1)).selectProvider("Org1", "Project1");
        verify(restTemplate, times(1)).postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(ChatResponse.class)
        );
    }

    @Test
    void testCreateChatCompletion_ClientError() {
        // Arrange
        when(providerService.selectProvider(any(), any())).thenReturn(provider);
        when(restTemplate.postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(ChatResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act
        ResponseEntity<ChatResponse> response = chatController.createChatCompletion(
                chatRequest, "Org1", "Project1"
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        // Verify mocks
        verify(providerService, times(1)).selectProvider("Org1", "Project1");
        verify(restTemplate, times(1)).postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(ChatResponse.class)
        );
    }

    @Test
    void testCreateChatCompletion_ServerError() {
        // Arrange
        when(providerService.selectProvider(any(), any())).thenReturn(provider);
        when(restTemplate.postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(ChatResponse.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        ResponseEntity<ChatResponse> response = chatController.createChatCompletion(
                chatRequest, "Org1", "Project1"
        );

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify mocks
        verify(providerService, times(1)).selectProvider("Org1", "Project1");
        verify(restTemplate, times(1)).postForEntity(
                eq("https://api.openai.com/v1/chat/completions"),
                any(HttpEntity.class),
                eq(ChatResponse.class)
        );
    }
}