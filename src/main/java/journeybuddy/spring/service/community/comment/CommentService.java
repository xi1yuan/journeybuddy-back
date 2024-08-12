package journeybuddy.spring.service.community.comment;

import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.web.dto.community.comment.CommentRequestDTO;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentService {
    Page<CommentResponseDTO> checkMyComment(String userEmail,Pageable pageable);
    CommentResponseDTO saveComment(String userEmail, Long postId, CommentRequestDTO request);
    void deleteComment(String userEmail, Long commentId);

}
