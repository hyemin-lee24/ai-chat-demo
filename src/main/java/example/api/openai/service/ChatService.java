package example.api.openai.service;

import example.api.openai.client.dto.OpenAiMessage;
import example.api.openai.client.dto.OpenAiResponse;
import example.api.openai.config.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final OpenAiClient openAiClient;
    private final Map<String, Deque<OpenAiMessage>> conversations = new ConcurrentHashMap<>();

    private static final String SYSTEM_PROMPT = """
            너는 챗봇 솔루션에 탑재된 AI 상담 어시스턴트이다.
            다음 원칙을 따른다.
            1. 항상 존댓말을 사용하고, 친절하지만 과하지 않게 답변한다.
            2. 질문 의도를 먼저 파악하고, 불명확하면 필요한 최소한의 추가 질문만 한다.
            3. 사실이 애매하거나 내부 정책/데이터가 없으면 아는 척하지 말고 "확인 필요"라고 안내한다.
            4. 보안/개인정보 관련 내용(주민번호, 카드번호 등)은 직접 입력받지 말고, 안전한 채널을 안내한다.
            5. 답변은 최대한 간결하게, 실무에서 바로 활용할 수 있도록 정리한다.
            """;

    private static final int MAX_MESSAGES = 20;

    public String getAnswer(String sessionId, String question) {
        String key = (sessionId == null || sessionId.isBlank()) ? "default" : sessionId;

        // 세션별 히스토리 가져오기 or 생성
        Deque<OpenAiMessage> history = conversations.computeIfAbsent(key, k -> {
            Deque<OpenAiMessage> deque = new ArrayDeque<>();
            deque.add(new OpenAiMessage("system", SYSTEM_PROMPT));
            return deque;
        });

        // 유저 메시지 추가
        history.add(new OpenAiMessage("user", question));
        trimHistory(history);

        // OpenAI 호출
        OpenAiResponse response = openAiClient.getChatCompletion(List.copyOf(history));

        String answer = Optional.ofNullable(response.getChoices())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getMessage().getContent())
                .orElse("죄송합니다. 지금은 답변을 생성하지 못했습니다.");

        // 어시스턴트 답변을 히스토리에 추가
        history.add(new OpenAiMessage("assistant", answer));
        trimHistory(history);

        return answer;
    }

    public void reset(String sessionId) {
        String key = (sessionId == null || sessionId.isBlank()) ? "default" : sessionId;
        Deque<OpenAiMessage> deque = new ArrayDeque<>();
        deque.add(new OpenAiMessage("system", SYSTEM_PROMPT));
        conversations.put(key, deque);
    }

    // 히스토리 제한 (토큰/비용/성능 고려)
    private void trimHistory(Deque<OpenAiMessage> history) {
        while (history.size() > MAX_MESSAGES) {
            // system 프롬프트는 유지하고 나머지 앞부분부터 제거
            OpenAiMessage first = history.peekFirst();
            if (first != null && "system".equals(first.getRole())) {
                Iterator<OpenAiMessage> it = history.iterator();
                it.next(); // system
                if (it.hasNext()) {
                    it.next();
                    it.remove();
                }
            } else {
                history.pollFirst();
            }
        }
    }
}
