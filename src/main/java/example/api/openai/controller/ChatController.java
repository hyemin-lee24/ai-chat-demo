package example.api.openai.controller;

import example.api.openai.dto.ChatRequest;
import example.api.openai.dto.ChatResponse;
import example.api.openai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Chat Request: {}", request);
        String answer = chatService.getAnswer(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(answer));
    }
}
