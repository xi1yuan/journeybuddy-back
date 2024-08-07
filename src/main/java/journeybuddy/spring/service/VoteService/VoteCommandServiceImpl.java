package journeybuddy.spring.service.VoteService;

import journeybuddy.spring.domain.*;
import journeybuddy.spring.domain.mapping.Place;
import journeybuddy.spring.repository.*;
import journeybuddy.spring.web.dto.VoteDTO.VoteRequestDTO;
import journeybuddy.spring.web.dto.VoteDTO.VoteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteCommandServiceImpl implements VoteCommandService {

    private static final Logger log = LoggerFactory.getLogger(VoteCommandServiceImpl.class);
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final PlanRepository planRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    //1. 투표 생성하기(플랜이 존재하지 않으면 예외처리, 플랜이 존재해야지만 투표기능 생성가능)
    //내가 만든 플랜에 대해서만 투표를 생성할 수 있음
    //여러개의 OptionText를 만들 수 있음.
    public List<VoteRequestDTO.VoteOptionRequestDTO> makeVote(VoteRequestDTO voteRequestDTO, String userEmail) {
        Long planId = voteRequestDTO.getPlanId();
        List<VoteRequestDTO.VoteOptionRequestDTO> options = voteRequestDTO.getOptions();

        // 플랜 존재 확인
        Optional<Plan> existingPlan = planRepository.findById(planId);
        if (existingPlan.isPresent()) {
            Plan plan = existingPlan.get();

            // 현재 사용자가 플랜의 생성자인지 확인
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));

            if (plan.getUser() == null) {
                throw new RuntimeException("Plan's creator information is missing.");
            }

            if (!plan.getUser().equals(user)) {
                throw new RuntimeException("You can only create votes for plans you have created.");
            }

            // 투표 객체 생성
            Vote vote = Vote.builder()
                    .plan(plan)
                    .title(voteRequestDTO.getTitle())
                    .description(voteRequestDTO.getDescription())
                    .startDate(voteRequestDTO.getStartDate())
                    .endDate(voteRequestDTO.getEndDate())
                    .build();

            // 투표 저장
            voteRepository.save(vote);
            log.info("투표 저장 성공. planId: {}, userEmail: {}", planId, userEmail);

            // 옵션 저장
            for (VoteRequestDTO.VoteOptionRequestDTO option : options) {
                VoteOption voteOption = VoteOption.builder()
                        .vote(vote)
                        .optionText(option.getOptionText())
                        .voteCount(0)
                        .build();
                voteOptionRepository.save(voteOption);
                log.info("투표 옵션 저장 성공. options: {}", option);

            }


            // 성공적으로 저장된 옵션 반환
            return options;
        } else {
            throw new RuntimeException("Plan with ID " + planId + " does not exist.");
        }
    }

    //2.투표하기
    public List<VoteOption> joinVote(Long voteId, List<Long> optionIds, Long userId) {
        // 투표가 존재하는지 확인
        Vote existingVote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));

        LocalDate currentDate = LocalDate.now();

        // 투표 기간이 유효한지 확인
        if (currentDate.isBefore(existingVote.getStartDate()) || currentDate.isAfter(existingVote.getEndDate())) {
            throw new RuntimeException("Voting period for Vote ID " + voteId + " is not valid.");
        }

        // 사용자의 기존 투표 기록을 확인합니다.
        List<VoteRecord> existingRecords = voteRecordRepository.findByUserIdAndVoteOption_VoteId(userId, voteId);
        Set<Long> existingOptionIds = existingRecords.stream()
                .map(record -> record.getVoteOption().getId())
                .collect(Collectors.toSet());

        // 선택된 옵션 중에서 이미 투표한 옵션을 제외합니다.
        List<Long> newOptionIds = optionIds.stream()
                .filter(optionId -> !existingOptionIds.contains(optionId))
                .collect(Collectors.toList());

        if (newOptionIds.isEmpty()) {
            throw new RuntimeException("No new options to vote for.");
        }

        // VoteOption을 가져와서 투표 수를 증가시킵니다.
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteIdAndIdIn(voteId, newOptionIds);
        if (voteOptions.size() != newOptionIds.size()) {
            throw new RuntimeException("Some options do not exist for Vote ID " + voteId);
        }

        List<VoteOption> joinedVoteOptions = new ArrayList<>();

        for (VoteOption voteOption : voteOptions) {
            // 투표 기록 추가
            VoteRecord record = VoteRecord.builder()
                    .user(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User with ID " + userId + " does not exist.")))
                    .voteOption(voteOption)
                    .build();
            voteRecordRepository.save(record);

            // 투표 수 증가
            voteOption.setVoteCount(voteOption.getVoteCount() + 1);
            voteOptionRepository.save(voteOption);

            joinedVoteOptions.add(voteOption);
            log.info("투표 옵션 업데이트 성공. optionId: {}, newVoteCount: {}", voteOption.getId(), voteOption.getVoteCount());
        }

        return joinedVoteOptions;
    }


    //투표 결과 확인
    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));
        // 해당 투표의 모든 VoteOption 조회
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);

        // 결과를 DTO로 변환
        List<VoteResponseDTO.VoteOptionResponseDTO> result = new ArrayList<>();
        for (VoteOption option : voteOptions) {
            VoteResponseDTO.VoteOptionResponseDTO dto = VoteResponseDTO.VoteOptionResponseDTO.builder()
                    .id(option.getId())
                    //        .optionText(option.getOptionText())
                    .voteCount(option.getVoteCount()) // 현재 투표 수 추가
                    .build();
            result.add(dto);
        }
        return result;
    }

    //투표 삭제하기(내가 투표 개설자일때만 삭제가능,즉 내가 만든 plan일때만!)
    public Vote deleteVote(Long voteId,String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));
        Plan plan = vote.getPlan();

        if (!plan.getUser().equals(user)) {
            throw new RuntimeException("You can only create votes for plans you have created.");
        }
        voteRepository.delete(vote);

        return vote;
    }
}



//3.투표항목을 만든후 투표한 사람을 count하고 결과를 도출하기(count는 위 로직에서 구현함)
    //4.투표할때 중복투표인지 확인하는 로직 추가하기
    //admin로직 추가
    //5. 결과 조회



