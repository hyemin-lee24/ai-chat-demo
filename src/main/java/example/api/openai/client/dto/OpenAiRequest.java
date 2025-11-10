package example.api.openai.client.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenAiRequest {
    private String model;
    private List<OpenAiMessage> messages;

    public OpenAiRequest(String model, List<OpenAiMessage> messages) {
        this.model = model;
        this.messages = messages;
    }
}
