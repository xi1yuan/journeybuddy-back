package journeybuddy.spring.web.controller;

import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.domain.Vote;
import journeybuddy.spring.domain.VoteOption;
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


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteRestController {
    private final VoteCommandService voteCommandService;
    private final VoteRepository voteRepository;

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
}
