package journeybuddy.spring.service.VoteService;

import journeybuddy.spring.domain.Plan;
import journeybuddy.spring.domain.Vote;
import journeybuddy.spring.domain.VoteOption;
import journeybuddy.spring.domain.mapping.Place;
import journeybuddy.spring.repository.PlaceRepository;
import journeybuddy.spring.repository.PlanRepository;
import journeybuddy.spring.repository.VoteOptionRepository;
import journeybuddy.spring.repository.VoteRepository;
import journeybuddy.spring.web.dto.VoteDTO.VoteRequestDTO;
import journeybuddy.spring.web.dto.VoteDTO.VoteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteCommandServiceImpl implements VoteCommandService {

    private final VoteRepository voteRepository;
    private final PlanRepository planRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PlaceRepository placeRepository;

    //1. 투표 생성하기(플랜이 존재하지 않으면 예외처리, 플랜이 존재해야지만 투표기능 생성가능)
    //한 투표에 여러개의 placeIds를 넣어야 함으로 List로 처리한다.
    public List<VoteRequestDTO.VoteOptionRequestDTO> makeVote(Long planId, List<Long> placeIds, String optionText) {
        Optional<Plan> existingPlan = planRepository.findById(planId);
        if (existingPlan.isPresent()) { //플랜이 존재하면 투표생성
            List<VoteRequestDTO.VoteOptionRequestDTO> voteOptions = new ArrayList<>();

            for (Long placeId : placeIds) {
                VoteRequestDTO.VoteOptionRequestDTO newVoteOptionRequestDTO = new VoteRequestDTO.VoteOptionRequestDTO();
                newVoteOptionRequestDTO.setPlaceId(placeId);
                newVoteOptionRequestDTO.setOptionText(optionText); // 동일한 optionText를 사용
                voteOptions.add(newVoteOptionRequestDTO);
            }
            return voteOptions;

        } else {
            throw new RuntimeException("Plan with ID " + planId + " does not exist.");


        }
    }

    //2.투표하기
    public List<VoteOption> joinVote(List<Long> placeIds, Long voteId,Long userId) {
        // 투표가 존재하는지 확인
        Vote existingVote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));

        LocalDate currentDate = LocalDate.now();

        // 투표 기간이 유효한지 확인
        if (currentDate.isBefore(existingVote.getStartDate()) || currentDate.isAfter(existingVote.getEndDate())) {
            throw new RuntimeException("Voting period for Vote ID " + voteId + " is not valid.");
        }

        List<VoteOption> joinedVoteOptions = new ArrayList<>();

        //중복투표인지 확인하는 로직
        if (voteRepository.checkVoteDuplication(userId, voteId)) {
            throw new RuntimeException("User with ID " + userId + " has already voted for Vote ID " + voteId + ".");
        }

        for (Long placeId : placeIds) {
            // 각 placeId에 대해 VoteOption을 생성하거나 업데이트
            VoteOption voteOption = voteOptionRepository.findById(voteId)
                    .orElseGet(() -> {
                        // Place 엔티티 조회
                        Place place = placeRepository.findById(placeId)
                                .orElseThrow(() -> new RuntimeException("Place with ID " + placeId + " does not exist."));

                        // 새로운 VoteOption 생성
                        return VoteOption.builder()
                                .vote(existingVote)
                                .place(place)
                                .voteCount(0) // 초기 투표 수 설정
                                .build();
                    });
            // 투표 수 증가
            voteOption.setVoteCount(voteOption.getVoteCount() + 1);
            voteOptionRepository.save(voteOption);
            joinedVoteOptions.add(voteOption);
        }

        return joinedVoteOptions;

    }

    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId){
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));
        // 해당 투표의 모든 VoteOption 조회
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);

        // 결과를 DTO로 변환
        List<VoteResponseDTO.VoteOptionResponseDTO> result = new ArrayList<>();
        for (VoteOption option : voteOptions) {
            VoteResponseDTO.VoteOptionResponseDTO dto = VoteResponseDTO.VoteOptionResponseDTO.builder()
                    .id(option.getId())
                    .placeId(option.getPlace().getPlaceId())
            //        .optionText(option.getOptionText())
                    .voteCount(option.getVoteCount()) // 현재 투표 수 추가
                    .build();
            result.add(dto);
        }
        return result;
    }
}

    //3.투표항목을 만든후 투표한 사람을 count하고 결과를 도출하기(count는 위 로직에서 구현함)
    //4.투표할때 중복투표인지 확인하는 로직 추가하기
    //admin로직 추가

    //5. 결과 조회



