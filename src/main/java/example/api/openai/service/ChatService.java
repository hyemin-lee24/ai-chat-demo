package example.api.openai.service;

import example.api.openai.client.dto.OpenAiResponse;
import example.api.openai.config.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final OpenAiClient openAiClient;

    public String getAnswer(String question) {
        OpenAiResponse openAiResponse = openAiClient.getChatCompletion(question);
        return openAiResponse.getChoices().get(0).getMessage().getContent();
    }
}
