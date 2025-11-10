package example.api.openai.client.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenAiMessage {
    private String role;
    private String content;

    public OpenAiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
