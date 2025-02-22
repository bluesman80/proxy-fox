package nl.jimkaplan.foxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRequest {
    private String model;
    @NonNull
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