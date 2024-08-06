package journeybuddy.spring.web.dto.VoteDTO;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class VoteRequestDTO {
    //vote를 생성함
    Long planId;
    Long placeId;
    Long userId; //삭제할 지 나중에 정하기
    String title;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    private List<VoteOptionRequestDTO> options;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoteOptionRequestDTO {
        private Long placeId;  // `Place` ID
        private String optionText;
    }

}
