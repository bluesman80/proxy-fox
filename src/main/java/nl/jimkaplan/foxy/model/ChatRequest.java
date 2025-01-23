package nl.jimkaplan.foxy.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private String model;
    private List<ChatMessage> messages;
    private Double temperature;
    private Double topP;
    private Integer n;
    private Boolean stream;
    private String stop;
    private Integer maxTokens;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private Map<String, Integer> logitBias;
    private String user;
}

