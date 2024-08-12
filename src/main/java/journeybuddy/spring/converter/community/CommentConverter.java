package journeybuddy.spring.converter.community;

import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.web.dto.community.comment.CommentRequestDTO;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import org.springframework.data.domain.Page;

public class CommentConverter {

    //comment에서 받아와서 toCommentResponseDTO로
    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getCommentId())
                .comment(comment.getComment())
                .userId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }

    //paging 에 필요한 converter
    public static Page<CommentResponseDTO> toCommentDTOPage(Page<Comment> comments) {
        Page<CommentResponseDTO> CommentDTOList =
                comments.map(page->CommentResponseDTO.builder()
                        .id(page.getCommentId())
                        .comment(page.getComment())
                        .userId(page.getUser().getId())
                        .postId(page.getPost().getId())
                        .build());
        return CommentDTOList;
    }
}
