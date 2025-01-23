package nl.jimkaplan.foxy.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Document(collection = "providers")
@Data
public class Provider {
    @Id
    private String id;
    private String name;
    private String url;
    private String apiKey;
    private boolean usageFlag;
    private int priority;
    private List<String> organizations;
    private List<String> projects;
}