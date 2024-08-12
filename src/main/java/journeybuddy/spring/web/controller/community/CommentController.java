package journeybuddy.spring.web.controller.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.community.comment.CommentService;
import journeybuddy.spring.web.dto.community.comment.CommentRequestDTO;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Tag(name = "Community Comment API", description = "커뮤니티 댓글 관련 API")
@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "댓글 작성 API")
    @PostMapping
    public ResponseEntity<CommentResponseDTO> saveComment(
            @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId) {
        CommentResponseDTO responseDTO = commentService.saveComment(userDetails.getUsername(), postId, request);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 API")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId) {
        commentService.deleteComment(userDetails.getUsername(), commentId);

        return ResponseEntity.ok(Map.of("message", "댓글 삭제 성공"));
    }

}
