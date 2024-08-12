package journeybuddy.spring.web.controller.community;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Community Scrap API", description = "커뮤니티 스크랩 관련 API")
@RestController
@RequestMapping("/posts/{postId}/scrap")
@RequiredArgsConstructor
public class ScrapController {

    // 스크랩 추가
    // 스크랩 삭제
    // 스크랩 여부 확인
    // 스크랩 리스트 조회
    // 스크랩한 게시글 조회
    // 스크랩한 게시글 리스트 조회
    // 스크랩한 게시글 상세 조회
}
