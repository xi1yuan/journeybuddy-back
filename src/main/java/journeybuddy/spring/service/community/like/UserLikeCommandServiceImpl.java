package journeybuddy.spring.service.community.like;

import journeybuddy.spring.converter.community.UserLikeConverter;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.community.UserLike;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.community.UserLikeRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLikeCommandServiceImpl implements UserLikeCommandService {

    private final UserLikeRepository userLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //좋아요 누르고 저장
    public UserLike saveLikes(String userEmail,Long postId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // 기존 UserLike가 있는지 확인
        Optional<UserLike> optionalUserLike = userLikeRepository.findByUserAndPost(user, post);

        if (optionalUserLike.isPresent()) {
            // 기존 UserLike가 있으면 반환
            return optionalUserLike.get();
        } else {
            // 기존 UserLike가 없으면 새로 생성
            UserLike newUserLike = UserLike.builder()
                    .user(user)
                    .post(post)
                    .build();
            return userLikeRepository.save(newUserLike);
        }
    }

    //내가 누른 좋아요 확인
    public Page<UserLikeResponesDTO> findMyLike(String userEmail,Pageable pageable){
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        Page<UserLike> likesByUser = userLikeRepository.findAllByUser(user, pageable);
        return likesByUser.map(UserLikeConverter::toUserLikeResponesDTO);


    }
}
