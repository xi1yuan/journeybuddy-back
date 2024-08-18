package journeybuddy.spring.web.dto.plan.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
public class TourInfoResponse {
    private List<TourInfoDTO> tourInfoList;

    @Builder
    public TourInfoResponse(List<TourInfoDTO> tourInfoList) {
        this.tourInfoList = tourInfoList;
    }

    @Data
    @NoArgsConstructor
    public static class TourInfoDTO {
        private String address;
        private String image;
        private String tel;
        private String name;

        @Builder
        public TourInfoDTO(String address, String image, String tel, String name) {
            this.address = address;
            this.image = image;
            this.tel = tel;
            this.name = name;
        }
    }
}
