package journeybuddy.spring.web.controller.community;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.community.like.UserLikeService;
import journeybuddy.spring.service.community.like.UserLikeServiceImpl;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Community Like API", description = "커뮤니티 좋아요 관련 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/like")
public class UserLikeController {

    private final UserLikeService userLikeService;
    private final UserLikeServiceImpl userLikeServiceImpl;

    @Operation(summary = "좋아요 누르기", description = "좋아요 누르기 API")
    @PostMapping
    public ResponseEntity<UserLikeResponesDTO> saveLikes(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable Long postId) {
        String userEmail = userDetails.getUsername();
        UserLikeResponesDTO userLikeResponesDTO = userLikeServiceImpl.saveLikes(userEmail, postId);
        return ResponseEntity.ok(userLikeResponesDTO);
    }

    @Operation(summary = "좋아요 취소", description = "좋아요 취소 API")
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteLikes(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable Long postId) {
        String userEmail = userDetails.getUsername();
        userLikeService.deleteLikes(userEmail, postId);
        return ResponseEntity.ok(Map.of("message", "좋아요 취소 성공"));
    }

}
