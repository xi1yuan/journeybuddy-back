package journeybuddy.spring.service.VoteService;

import journeybuddy.spring.web.dto.VoteDTO.VoteRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteCommandService {
    //optionText만 사용할것임. placeIds는 필요없음
    List<VoteRequestDTO.VoteOptionRequestDTO> makeVote(Long planId, List<Long> placeIds, String optionText);
}
