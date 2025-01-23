package nl.jimkaplan.foxy.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<ChatChoice> choices;
    private Usage usage;
}
