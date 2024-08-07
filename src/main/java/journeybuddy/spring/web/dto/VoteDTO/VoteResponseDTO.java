package journeybuddy.spring.web.dto.VoteDTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponseDTO {
    Long id;
    String title;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    Long planId;
    Long placeId;
    boolean isAdmin; //권한이 있는 사용자인지
    boolean canVote; //중복투표인지 확인
    List<VoteOptionResponseDTO> options;
    LocalDate createdAt;
    LocalDate updatedAt;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VoteOptionResponseDTO {
        Long id;
        String optionText;  // 옵션의 설명 또는 텍스트
        Integer voteCount;

    }
}
