package example.api.openai.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRequest {
    private String message;
    public ChatRequest(String message) {
        this.message = message;
    }
}
