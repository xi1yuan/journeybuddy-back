package journeybuddy.spring.service.VoteService;

import journeybuddy.spring.domain.Vote;
import journeybuddy.spring.domain.VoteOption;
import journeybuddy.spring.web.dto.VoteDTO.VoteRequestDTO;
import journeybuddy.spring.web.dto.VoteDTO.VoteResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteCommandService {
    //optionText만 사용할것임. placeIds는 필요없음
    public List<VoteRequestDTO.VoteOptionRequestDTO> makeVote(VoteRequestDTO voteRequestDTO, String userEmail);
    public List<VoteOption> joinVote(Long voteId, Long userId);
    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId);
    public Vote deleteVote(Long voteId,String userEmail);



}
