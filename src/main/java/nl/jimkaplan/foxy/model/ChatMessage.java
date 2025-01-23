package nl.jimkaplan.foxy.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ChatMessage {
    @NonNull
    private String content;
    @NonNull
    private String role;
    private String name;
}