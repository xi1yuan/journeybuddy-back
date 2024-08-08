package journeybuddy.spring.service.plan.vote;

import journeybuddy.spring.domain.vote.Vote;
import journeybuddy.spring.domain.vote.VoteOption;
import journeybuddy.spring.web.dto.plan.vote.VoteRequestDTO;
import journeybuddy.spring.web.dto.plan.vote.VoteResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteCommandService {
    //optionText만 사용할것임. placeIds는 필요없음
    public VoteRequestDTO makeVote(VoteRequestDTO voteRequestDTO, String userEmail);
    public List<VoteOption> joinVote(Long voteId, List<Long> optionIds, Long userId);
    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId);
    public Vote deleteVote(Long voteId,String userEmail);



}
