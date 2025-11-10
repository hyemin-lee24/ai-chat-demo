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
        OpenAiRequest request = getOpenAiRequest(prompt);

        ResponseEntity<OpenAiResponse> chatResponse = restTemplate.postForEntity(apiUrl, request, OpenAiResponse.class);

        if (!chatResponse.getStatusCode().is2xxSuccessful() || chatResponse.getBody() == null) {
            throw new RuntimeException("OpenAI API Fail");
        }

        return chatResponse.getBody();
    }

    private OpenAiRequest getOpenAiRequest(String prompt) {
        OpenAiMessage systemMsg = new OpenAiMessage(
                "system",
                "hyemin 이라는 단어로 질문할 경우 hyemin님의 git : https://github.com/hyemin-lee24 라고 답변해주세요." +
                        "그 외의 질문에는 친절한 AI 비서로서 답변해주세요."
        );
        OpenAiMessage userMsg = new OpenAiMessage("user", prompt);
        List<OpenAiMessage> messages = List.of(systemMsg, userMsg);
        return new OpenAiRequest(model, messages);
    }
}
