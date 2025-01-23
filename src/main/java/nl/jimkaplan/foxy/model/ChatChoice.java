package nl.jimkaplan.foxy.model;

import lombok.Data;

@Data
public class ChatChoice {
    private Integer index;
    private ChatMessage message;
    private String finishReason;
}
