package journeybuddy.spring.service.plan.create;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import journeybuddy.spring.web.dto.plan.response.CityResponse;
import journeybuddy.spring.web.dto.plan.response.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelApiServiceImpl implements TravelApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService1/areaCode1";
    private static final int NUM_OF_ROWS = 100;

    @Value("${travel.api.key}")
    private String travelApiKey;

    @Autowired
    public TravelApiServiceImpl(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public List<ProvinceResponse> getProvinces() {
        List<Map<String, Object>> areaCodes = fetchAreaCodes(null);
        List<ProvinceResponse> provinces = new ArrayList<>();
        for (Map<String, Object> areaCode : areaCodes) {
            ProvinceResponse response = ProvinceResponse.builder()
                    .code((String) areaCode.get("code"))
                    .name((String) areaCode.get("name"))
                    .build();
            provinces.add(response);
        }
        return provinces;
    }

    public List<CityResponse> getCities(String provinceCode) {
        List<Map<String, Object>> areaCodes = fetchAreaCodes(provinceCode);
        List<CityResponse> cities = new ArrayList<>();
        for (Map<String, Object> areaCode : areaCodes) {
            CityResponse response = CityResponse.builder()
                    .code((String) areaCode.get("code"))
                    .name((String) areaCode.get("name"))
                    .build();
            cities.add(response);
        }
        return cities;
    }

    private List<Map<String, Object>> fetchAreaCodes(String areaCode) {
        List<Map<String, Object>> allResults = new ArrayList<>();
        int pageNo = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                URI uri = buildUri(areaCode, pageNo);
                String response = restTemplate.getForObject(uri, String.class);
                List<Map<String, Object>> pageResults = parseAreaCodes(response);
                allResults.addAll(pageResults);
                hasMoreData = pageResults.size() == NUM_OF_ROWS;
                pageNo++;
            } catch (HttpClientErrorException e) {
                log.error("Error fetching area codes: {}", e.getMessage());
                break;
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error creating URI for area code API request", e);
            }
        }

        return allResults;
    }

    private URI buildUri(String areaCode, int pageNo) throws URISyntaxException {
        StringBuilder uriBuilder = new StringBuilder(BASE_URL)
                .append("?serviceKey=").append(travelApiKey)
                .append("&MobileOS=ETC&MobileApp=AppTest&_type=json")
                .append("&numOfRows=").append(NUM_OF_ROWS)
                .append("&pageNo=").append(pageNo);

        if (areaCode != null) {
            uriBuilder.append("&areaCode=").append(areaCode);
        }

        return new URI(uriBuilder.toString());
    }

    private List<Map<String, Object>> parseAreaCodes(String response) {
        List<Map<String, Object>> areaList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    Map<String, Object> map = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
                    areaList.add(map);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing area codes: {}", e.getMessage());
        }
        return areaList;
    }
}
