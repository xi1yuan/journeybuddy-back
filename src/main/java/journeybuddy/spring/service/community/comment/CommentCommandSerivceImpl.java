package journeybuddy.spring.service.community.comment;

import journeybuddy.spring.converter.community.CommentConverter;
import journeybuddy.spring.domain.community.Comment;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.community.CommentRepository;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCommandSerivceImpl implements CommentCommandService{

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

/*
    @Override
    public Page<CommentResponseDTO> checkMyComment(String userEmail, Pageable pageable) {
        try {
            if (userEmail != null) {
                Page<Comment> myComment = commentRepository.findAllByUser(userEmail, pageable);
                commentRepository.findAll();
                return myComment.map(CommentConverter::toCommentResponseDTO);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }
*/



    public Page<CommentResponseDTO> checkMyComment(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(()->{
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
    @Override
    public Comment commentSave(String userEmail, Long postId, Comment comment) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
        Post post  = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
            comment.setUser(user);
            comment.setPost(post);
            Comment savedComment = commentRepository.save(comment);
            return savedComment;
        }



}
