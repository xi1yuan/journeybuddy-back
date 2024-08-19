package journeybuddy.spring.service.plan.create;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenAIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String generateTravelPlan(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4-turbo");
        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
        if (response != null && response.containsKey("choices")) {
            Map<String, Object> choice = (Map<String, Object>) ((List<Map<String, Object>>) response.get("choices")).get(0);
            Map<String, Object> messageContent = (Map<String, Object>) choice.get("message");
            return (String) messageContent.get("content");
        }
        return "Error generating travel plan";
    }
}
