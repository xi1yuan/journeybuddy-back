package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.response.CoordinateResponse;
import journeybuddy.spring.web.dto.plan.response.PlaceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
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

    // 카카오 맵에서 장소 검색 후 여러 장소 정보를 리스트로 반환
    public List<PlaceResponse> getPlace(String keyword) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoMapApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<PlaceResponse> places = new ArrayList<>();

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            log.info("API Response: {}", responseEntity.getBody());

            if (response != null && response.containsKey("documents")) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                log.info("documents: {}", documents.toString());
                for (Map<String, Object> document : documents) {
                    String name = document.getOrDefault("place_name", "").toString();
                    String roadAddress = document.getOrDefault("road_address_name", "").toString();
                    String place_url = document.getOrDefault("place_url", "").toString();
                    String tel = document.getOrDefault("phone", "").toString();
                    places.add(PlaceResponse.builder()
                            .name(name)
                            .address(roadAddress)
                            .url(place_url)
                            .tel(tel)
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch places: " + e.getMessage(), e);
        }
        return places;
    }

    // 카카오 맵에서 주소로 해당 장소의 정보를 반환
    public PlaceResponse getPlaceInfo(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoMapApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            log.info("API Response: {}", responseEntity.getBody());
            if (response != null && response.containsKey("documents")) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                if (!documents.isEmpty()) {
                    Map<String, Object> firstDocument = documents.get(0);
                    Map<String, Object> roadAddress = (Map<String, Object>) firstDocument.get("road_address");

                    String name = roadAddress != null ? (String) roadAddress.get("building_name") : "";
                    String roadAddr = roadAddress != null ? (String) roadAddress.get("address_name") : "";
                    String longitude = firstDocument.get("x").toString();
                    String latitude = firstDocument.get("y").toString();

                    return PlaceResponse.builder()
                            .name(name)
                            .address(roadAddr)
                            .url("https://map.kakao.com/link/map/" + name + "," + latitude + "," + longitude)
                            .longitude(longitude)
                            .latitude(latitude)
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch place info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch place info: " + e.getMessage(), e);
        }
        return null;
    }


}
