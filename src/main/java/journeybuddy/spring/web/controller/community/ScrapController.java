package journeybuddy.spring.web.controller.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.community.scrap.ScrapService;
import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Community Scrap API", description = "커뮤니티 스크랩 관련 API")
@RestController
@RequestMapping("/posts/{postId}/scrap")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;

    // 스크랩 추가
    @Operation(summary = "스크랩 추가", description = "스크랩 추가 API")
    @PostMapping
    public ResponseEntity<ScrapResponseDTO> saveScrap(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable Long postId) {
        String userEmail = userDetails.getUsername();
        ScrapResponseDTO scrapResponseDTO = scrapService.saveScrap(userEmail, postId);
        return ResponseEntity.ok(scrapResponseDTO);

    }

    // 스크랩 삭제
    @Operation(summary = "스크랩 삭제", description = "스크랩 삭제 API")
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteScrap(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable Long postId) {
        String userEmail = userDetails.getUsername();
        scrapService.deleteScrap(userEmail, postId);
        return ResponseEntity.ok(Map.of("message", "스크랩 삭제 성공"));
    }

}
