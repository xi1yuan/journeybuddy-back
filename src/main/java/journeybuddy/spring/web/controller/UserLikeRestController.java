package journeybuddy.spring.web.controller;


import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.UserLikeConverter;
import journeybuddy.spring.domain.UserLike;
import journeybuddy.spring.service.UserLikeService.UserLikeCommandService;
import journeybuddy.spring.service.UserLikeService.UserLikeCommandServiceImpl;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeRequestDTO;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeResponesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my_page/userlikes")
public class UserLikeRestController {

    private final UserLikeCommandService userLikeCommandService;
    private final UserLikeCommandServiceImpl userLikeCommandServiceImpl;

    @PostMapping("/save/{postId}")
    public ApiResponse<UserLikeRequestDTO> save(@PathVariable("postId") Long postId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received request: {}", postId);
        String userEmail = userDetails.getUsername();
        UserLike savedLikes = userLikeCommandService.saveLikes(userEmail,postId);
        // 저장된 UserLike를 DTO로 변환하여 응답
        UserLikeRequestDTO responseDTO = UserLikeConverter.toUserLikeRequestDTO(savedLikes);

        return ApiResponse.onSuccess(responseDTO);

    }

    @GetMapping("/myLikes")
    public ApiResponse<Page<UserLikeResponesDTO>> findMyLikes(@AuthenticationPrincipal UserDetails userDetails,
                                                              Pageable pageable) {
        String userEmail = userDetails.getUsername();
        Page<UserLikeResponesDTO> likesPage = userLikeCommandServiceImpl.findMyLike(userEmail,pageable);
        return ApiResponse.onSuccess(likesPage);
    }
}
