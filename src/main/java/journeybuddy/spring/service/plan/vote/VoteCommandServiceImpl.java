package journeybuddy.spring.service.plan.vote;

import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.vote.Vote;
import journeybuddy.spring.domain.vote.VoteOption;
import journeybuddy.spring.domain.vote.VoteRecord;
import journeybuddy.spring.repository.plan.PlanRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.repository.vote.VoteOptionRepository;
import journeybuddy.spring.repository.vote.VoteRecordRepository;
import journeybuddy.spring.repository.vote.VoteRepository;
import journeybuddy.spring.web.dto.plan.vote.VoteRequestDTO;
import journeybuddy.spring.web.dto.plan.vote.VoteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    public VoteResponseDTO.VoteMakeResponseDTO makeVote(VoteRequestDTO voteRequestDTO, String userEmail) {
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
                throw new RuntimeException("플랜게설자가 없습니다.");
            }

            if (!plan.getUser().equals(user)) {
                throw new RuntimeException("플랜 주인만 투표를 개설할 수 있습니다.");
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

            List<VoteResponseDTO.VoteInfoDTO> savedOptions = new ArrayList<>();
            for (VoteRequestDTO.VoteOptionRequestDTO optionRequest : options) {  // VoteOptionRequestDTO 타입 사용
                // 옵션을 저장
                VoteOption voteOption = VoteOption.builder()
                        .vote(vote)
                        .optionText(optionRequest.getOptionText())
                        .voteCount(0)
                        .build();
                voteOptionRepository.save(voteOption);
                log.info("투표 옵션 저장 성공. options: {}", optionRequest.getOptionText());

                // 저장된 옵션을 포함한 VoteInfoDTO 생성
                VoteResponseDTO.VoteInfoDTO optionDTO = new VoteResponseDTO.VoteInfoDTO();
                optionDTO.setOptionId(voteOption.getId());  // 저장된 ID를 사용
                optionDTO.setOptionText(voteOption.getOptionText());

                savedOptions.add(optionDTO);
            }

            // 저장된 옵션 리스트를 포함하여 새로운 VoteRequestDTO 객체 생성
            VoteResponseDTO.VoteMakeResponseDTO responseDTO = new VoteResponseDTO.VoteMakeResponseDTO();
            responseDTO.setPlanId(planId);
            responseDTO.setVoteId(vote.getId());
            responseDTO.setTitle(vote.getTitle());
            responseDTO.setDescription(vote.getDescription());
            responseDTO.setStartDate(vote.getStartDate());
            responseDTO.setEndDate(vote.getEndDate());
            responseDTO.setOptions(savedOptions);


            // 전체 투표 정보가 포함된 VoteRequestDTO 객체 반환
            return responseDTO;
        } else {
            throw new RuntimeException("Plan with ID " + planId + " does not exist.");
        }
    }

    //2.투표하기(기간만료, 한 옵션에 2번투표하는경우에는 에러발생, 한 투표자가 여러항목에 투표가능)
    public List<VoteResponseDTO.VoteOptionResponseDTO> joinVote(Long voteId, List<Long> optionIds, Long userId) {
        // 투표가 존재하는지 확인
        Vote existingVote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));

        //    LocalDate currentDate = LocalDate.now();

        LocalDateTime today = LocalDateTime.now();

        // 투표 기간이 유효한지 확인
        if (today.isBefore(existingVote.getStartDate()) || today.isAfter(existingVote.getEndDate())) {
            throw new RuntimeException("Voting period for Vote ID " + voteId + " is not valid.");
        }


        if (optionIds == null) {
            optionIds = Collections.emptyList();
        }


        // 사용자의 기존 투표 기록을 확인(set은 중복허용안함, set을 사용해서 optionId가 중복인지 아닌지 확인함)
        List<VoteRecord> existingRecords = voteRecordRepository.findByUserIdAndVoteOption_VoteId(userId, voteId);
        Set<Long> existingOptionIds = existingRecords.stream()
                .map(record -> record.getVoteOption().getId())
                .collect(Collectors.toSet());


        // 선택된 옵션 중에서 이미 투표한 옵션을 제외
        List<Long> newOptionIds = optionIds.stream()
                .filter(optionId -> !existingOptionIds.contains(optionId))
                .collect(Collectors.toList());




        if (newOptionIds.isEmpty()) {
            throw new RuntimeException("한 항목에 한번만 투표가능합니다.");
        }

        // VoteOption을 가져와서 투표 수를 증가시킵니다.
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteIdAndIdIn(voteId, newOptionIds);
        if (voteOptions.size() != newOptionIds.size()) {
            throw new RuntimeException("없는 투표항목입니다." + voteId);
        }

        List<VoteResponseDTO.VoteOptionResponseDTO> joinedVoteOptions = new ArrayList<>();

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

            // VoteOption을 VoteOptionResponseDTO로 변환
            VoteResponseDTO.VoteOptionResponseDTO responseDTO = VoteResponseDTO.VoteOptionResponseDTO.builder()
                    .id(voteOption.getId())
                    .optionText(voteOption.getOptionText())  // 옵션의 설명 또는 텍스트
                    .voteCount(voteOption.getVoteCount())
                    .userIds(Collections.singletonList(userId))  // 투표한 사용자 ID 리스트
                    .build();

            // 변환된 DTO를 리스트에 추가
            joinedVoteOptions.add(responseDTO);

            log.info("투표 옵션 업데이트 성공. optionId: {}, newVoteCount: {}", voteOption.getId(), voteOption.getVoteCount());
        }

        return joinedVoteOptions;
    }


    //3. 투표 결과 확인,누가 투표했는지 userId를 response로 반환함
    public List<VoteResponseDTO.VoteOptionResponseDTO> checkVoteResult(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));
        // 해당 투표의 모든 VoteOption 조회
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);

        List<VoteResponseDTO.VoteOptionResponseDTO> result = new ArrayList<>();
        for (VoteOption option : voteOptions) {
            // 각 옵션에 대한 투표 기록 조회
            List<VoteRecord> voteRecords = voteRecordRepository.findByVoteOptionId(option.getId());
            // 투표한 사용자 ID 목록 생성
            List<Long> userIds = voteRecords.stream()
                    .map(record -> record.getUser().getId())
                    .collect(Collectors.toList());

            // 결과를 DTO로 변환
            VoteResponseDTO.VoteOptionResponseDTO dto = VoteResponseDTO.VoteOptionResponseDTO.builder()
                    .id(option.getId())
                    .optionText(option.getOptionText())
                    .userIds(userIds)
                    .voteCount(option.getVoteCount()) // 현재 투표 수 추가
                    .build();
            result.add(dto);
        }
        return result;
    }


    //4. 투표 삭제하기(내가 투표 개설자일때만 삭제가능,즉 내가 만든 plan일때만!)
    public Vote deleteVote(Long voteId,String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));
        Plan plan = vote.getPlan();

        if (!plan.getUser().equals(user)) {
            throw new RuntimeException("You can only create votes for plans you have created.");
        }
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);
        for (VoteOption option : voteOptions) {
            // 관련된 투표 기록 삭제
            voteRecordRepository.deleteByVoteOptionId(option.getId());
        }
        voteRepository.delete(vote);

        return vote;
    }

    //투표 취소하기. 내가 투표한 항목 취소하고 voteCount원래대로 되돌림

    public List<VoteResponseDTO.VoteOptionResponseDTO> rollBackVote(Long voteId,String userEmail) {
        //투표,유저,플랜이 존재하는지 확인

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User with email " + userEmail + " does not exist."));
        Long userId = user.getId();

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote with ID " + voteId + " does not exist."));


        // 투표 옵션 리스트 가져오기
        List<VoteOption> voteOptions = voteOptionRepository.findByVoteId(voteId);
        log.info("Vote Options: {}", voteOptions);

        // 투표 옵션 IDs 리스트 생성
        List<Long> optionIds = voteOptions.stream()
                .map(VoteOption::getId)
                .collect(Collectors.toList());
        log.info("Option IDs: {}", optionIds);

        // 사용자의 투표 기록 찾기
        List<VoteRecord> voteRecords = voteRecordRepository.findByUser_IdAndVoteOption_IdIn(userId, optionIds);
        log.info("Vote Records: {}", voteRecords);

        //꼭 Long타입으로 바꿔줘야 터미널에서 조회가능
        List<Long> records = voteRecords.stream()
                .map(VoteRecord::getId)
                .collect(Collectors.toList());
        log.info("Vote Long type Records: {}", records);


        // VoteRecord에서 vote_option_id 추출
        List<Long> voteOptionIds = voteRecords.stream()
                .map(voteRecord -> voteRecord.getVoteOption().getId())
                .distinct()
                .collect(Collectors.toList());


        log.info("voteRecord 외래키 {}", voteOptionIds);

        if(voteRecords.isEmpty()) {
            throw new RuntimeException("투표한적없습니다");
        }

        // 모든 투표 기록 삭제
        voteRecordRepository.deleteAll(voteRecords);
        log.info("투표 기록 삭제 완료:{},삭제된 투표 옵션Id:{}",voteId, records);

        // 투표 수 조정 및 응답 DTO 생성
        List<VoteResponseDTO.VoteOptionResponseDTO> result = new ArrayList<>();
        for (VoteOption option : voteOptions) {
            log.info("Processing option with ID: {}", option.getId());
            log.info("voteOptions1:{}", voteOptionIds);
            // 각 옵션에 대한 투표 기록 조회
            if(voteOptionIds.contains(option.getId())) {

                List<VoteRecord> optionVoteRecords = voteRecordRepository.findByVoteOptionId(option.getId());
                // 투표한 사용자 ID 목록 생성
                List<Long> userIds = optionVoteRecords.stream()
                        .map(record -> record.getUser().getId())
                        .collect(Collectors.toList());

                // 투표 수 감소
                option.setVoteCount(option.getVoteCount() - 1); // userIds.size()로 감소

                // 저장
                voteOptionRepository.save(option);


                // 현재 상태를 DTO로 저장
                VoteResponseDTO.VoteOptionResponseDTO responseDTO = VoteResponseDTO.VoteOptionResponseDTO.builder()
                        .id(option.getId())
                        .optionText(option.getOptionText())
                        .voteCount(option.getVoteCount())
                        .userIds(userIds)
                        .build();

                result.add(responseDTO);
            }
        }
        log.info("Final vote options with counts: {}", result);
        return result;

    }
}