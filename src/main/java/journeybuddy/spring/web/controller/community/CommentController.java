package journeybuddy.spring.web.controller.community;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Community Comment API", description = "커뮤니티 댓글 관련 API")
@RestController
@RequestMapping("/posts/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {
}
