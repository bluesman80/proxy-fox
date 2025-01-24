package nl.jimkaplan.foxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {
    @NonNull
    private String content;
    @NonNull
    private String role;
    private String name;
}