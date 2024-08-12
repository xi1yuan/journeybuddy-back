package journeybuddy.spring.service.community.comment;

import jakarta.transaction.Transactional;
import journeybuddy.spring.converter.community.CommentConverter;
import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.community.CommentRepository;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.comment.CommentRequestDTO;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentSerivceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentResponseDTO saveComment(String userEmail, Long postId, CommentRequestDTO request) {
        User user = checkUser(userEmail);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .comment(request.getComment())
                .build();

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        Comment savedComment = commentRepository.save(comment);
        return CommentConverter.toCommentResponseDTO(savedComment);
    }

    public void deleteComment(String userEmail, Long commentId) {
        User user = checkUser(userEmail);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다"));
        Post post = comment.getPost();

        // 해당 user가 작성한 댓글인지 확인
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다");
        }

        // 댓글 삭제
        commentRepository.delete(comment);
        post.setCommentCount(post.getCommentCount() - 1);
    }


    public Page<CommentResponseDTO> checkMyComment(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
            return new UsernameNotFoundException("User not found with email: " + userEmail);
        });
        Page<Comment> commentsByUser = commentRepository.findAllByUser(user, pageable); // 사용자 객체를 인자로 전달
        return commentsByUser.map(CommentConverter::toCommentResponseDTO);
    }

    private User checkUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }

}
