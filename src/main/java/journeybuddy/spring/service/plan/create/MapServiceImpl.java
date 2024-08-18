package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.response.CoordinateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    @Value("${kakaomap.api.key}")
    private String kakaoMapApiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public MapServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    // 카카오 맵에서 주소로 해당 장소의 위도, 경도 추출
    public CoordinateResponse getCoordinate(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoMapApiKey); // API 키를 헤더에 추가

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            if (response != null && response.containsKey("documents")) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                if (!documents.isEmpty()) {
                    Map<String, Object> firstDocument = documents.get(0);
                    double latitude = Double.parseDouble(firstDocument.getOrDefault("y", "0").toString());
                    double longitude = Double.parseDouble(firstDocument.getOrDefault("x", "0").toString());
                    return CoordinateResponse.builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch coordinates: " + e.getMessage(), e);
        }
        return null;
    }
}
