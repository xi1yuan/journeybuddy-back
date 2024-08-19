package journeybuddy.spring.service.plan.create;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import journeybuddy.spring.web.dto.plan.response.CityResponse;
import journeybuddy.spring.web.dto.plan.response.ProvinceResponse;
import journeybuddy.spring.web.dto.plan.response.TourInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourApiServiceImpl implements TourApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String AREA_BASE_URL = "https://apis.data.go.kr/B551011/KorService1/areaCode1";
    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService1/areaBasedList1";
    private static final int NUM_OF_ROWS = 100;

    @Value("${travel.api.key}")
    private String travelApiKey;

    @Autowired
    public TourApiServiceImpl(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
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

    public TourInfoResponse getTourInfo(String areaCode, String sigunguCode, String preference) {
        List<Map<String, Object>> places = getPlaces(areaCode,sigunguCode, getContentTypeByPreference(preference));
        List<TourInfoResponse.TourInfoDTO> response = new ArrayList<>();
        for (Map<String, Object> place : places) {
            TourInfoResponse.TourInfoDTO dto = TourInfoResponse.TourInfoDTO.builder()
                    .address((String) place.get("address"))
                    .image((String) place.get("image"))
                    .tel((String) place.get("tel"))
                    .name((String) place.get("name"))
                    .build();
            response.add(dto);
        }

        return TourInfoResponse.builder()
                .tourInfoList(response)
                .build();
    }

    public List<Map<String, Object>> getPlaces(String areaCode, String sigunguCode, String contentTypeId) {
        List<Map<String, Object>> allResults = new ArrayList<>();
        int pageNo = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                URI uri = buildTourInfoUri(areaCode, sigunguCode, contentTypeId, pageNo);
                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
                log.error("API Request URI: {}", uri);
                log.error("API Response: {}", response);

                List<Map<String, Object>> pageResults = parseTourInfo(response.getBody());
                List<Map<String, Object>> filteredResults = filterTourInfo(pageResults);
                allResults.addAll(filteredResults);

                hasMoreData = pageResults.size() == NUM_OF_ROWS;
                pageNo++;
            } catch (HttpClientErrorException | URISyntaxException e) {
                log.error("Error fetching tour info: {}", e.getMessage());
                break;
            }
        }

        return allResults;
    }

    private URI buildTourInfoUri(String areaCode, String sigunguCode, String contentTypeId, int pageNo) throws URISyntaxException {
        return new URI(BASE_URL + "?serviceKey=" + travelApiKey +
                "&areaCode=" + areaCode +
                "&sigunguCode=" + sigunguCode +
                "&contentTypeId=" + contentTypeId +
                "&MobileOS=ETC&MobileApp=AppTest&_type=json" +
                "&numOfRows=" + NUM_OF_ROWS +
                "&pageNo=" + pageNo);
    }

    private List<Map<String, Object>> parseTourInfo(String response) {
        List<Map<String, Object>> tourList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    Map<String, Object> map = objectMapper.convertValue(item, new TypeReference<>() {});
                    tourList.add(map);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing tour info: {}", e.getMessage());
        }
        return tourList;
    }

    private List<Map<String, Object>> filterTourInfo(List<Map<String, Object>> pageResults) {
        List<Map<String, Object>> filteredList = new ArrayList<>();
        for (Map<String, Object> entry : pageResults) {
            Map<String, Object> filteredMap = new HashMap<>();
            filteredMap.put("address", entry.get("addr1"));
            filteredMap.put("image", entry.get("firstimage"));
            filteredMap.put("tel", entry.get("tel"));
            filteredMap.put("name", entry.get("title"));
            filteredList.add(filteredMap);
        }
        return filteredList;
    }

    private String getContentTypeByPreference(String preference) {
        switch (preference.toLowerCase()) {
            case "레포츠":
                return "28";
            case "관광지":
                return "12";
            case "문화시설":
                return "14";
            case "숙박":
                return "32";
            case "음식점":
                return "39";
            default:
                return "12"; // 기본값
        }
    }

    private List<Map<String, Object>> fetchAreaCodes(String areaCode) {
        List<Map<String, Object>> allResults = new ArrayList<>();
        int pageNo = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                URI uri = buildAreaCodeUri(areaCode, pageNo);
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

    private URI buildAreaCodeUri(String areaCode, int pageNo) throws URISyntaxException {
        StringBuilder uriBuilder = new StringBuilder(AREA_BASE_URL)
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
