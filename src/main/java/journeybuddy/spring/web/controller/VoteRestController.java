package journeybuddy.spring.web.controller;

import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.domain.Vote;
import journeybuddy.spring.domain.VoteOption;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.repository.VoteRepository;
import journeybuddy.spring.service.VoteService.VoteCommandService;
import journeybuddy.spring.web.dto.VoteDTO.VoteRequestDTO;
import journeybuddy.spring.web.dto.VoteDTO.VoteResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteRestController {
    private final VoteCommandService voteCommandService;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    @PostMapping("/make_vote")
    public ApiResponse<List<VoteRequestDTO.VoteOptionRequestDTO>> makeVote(
            @RequestBody VoteRequestDTO voteRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 유저 확인
        String userEmail = userDetails.getUsername();
        log.info("Received VoteRequestDTO: " + userEmail);
        List<VoteRequestDTO.VoteOptionRequestDTO> options = voteCommandService.makeVote(voteRequestDTO, userEmail);
        return ApiResponse.onSuccess(options);
    }

    @PostMapping("/join_vote")
    public ApiResponse<List<VoteOption>>joinVote(@RequestParam Long voteId,
                                                 @RequestParam List<Long> optionIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        log.info("투표한 사용자: " + userEmail);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            long userId = user.getId(); // User 객체에서 id를 long 타입으로 가져옵니다.

            // voteCommandService.joinVote를 호출하여 선택한 옵션들에 투표를 처리합니다.
            List<VoteOption> options = voteCommandService.joinVote(voteId, optionIds, userId);
            return ApiResponse.onSuccess(options);
        }
        return null;
    }
}
