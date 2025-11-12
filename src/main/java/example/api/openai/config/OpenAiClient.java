package example.api.openai.config;

import example.api.openai.client.dto.OpenAiMessage;
import example.api.openai.client.dto.OpenAiRequest;
import example.api.openai.client.dto.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {
    private final RestTemplate restTemplate;

    @Value("${spring.ai.openai.api-url}")
    private String apiUrl;

    @Value("${spring.ai.openai.model}")
    private String model;

    public OpenAiResponse getChatCompletion(String prompt) {
        OpenAiMessage systemMsg = new OpenAiMessage(
                "system",
                "너는 친절한 AI 어시스턴트야. 사용자의 질문에 간결하고 명확하게 답변해."
        );
        OpenAiMessage userMsg = new OpenAiMessage("user", prompt);
        return getChatCompletion(List.of(systemMsg, userMsg));
    }

    public OpenAiResponse getChatCompletion(List<OpenAiMessage> messages) {
        OpenAiRequest request = new OpenAiRequest(model, messages);

        ResponseEntity<OpenAiResponse> chatResponse =
                restTemplate.postForEntity(apiUrl, request, OpenAiResponse.class);

        if (!chatResponse.getStatusCode().is2xxSuccessful() || chatResponse.getBody() == null) {
            log.error("OpenAI API error. status={}, body={}",
                    chatResponse.getStatusCode(),
                    chatResponse.getBody());
            throw new RuntimeException("OpenAI API Fail");
        }

        return chatResponse.getBody();
    }
}
