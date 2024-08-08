package journeybuddy.spring.service.community.comment;

import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentCommandService {
    Page<CommentResponseDTO> checkMyComment(String userEmail,Pageable pageable);
    Comment commentSave(String userEmail,Long postId, Comment comment);

}
