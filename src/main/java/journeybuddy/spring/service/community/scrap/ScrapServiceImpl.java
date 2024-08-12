package journeybuddy.spring.service.community.scrap;

import journeybuddy.spring.converter.community.ScrapConverter;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.community.Scrap;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.community.ScrapRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //스크랩하기
    public ScrapResponseDTO saveScrap(String userEmail, Long postId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Scrap newScrap = Scrap.builder()
                .user(user)
                .post(post)
                .build();

        scrapRepository.save(newScrap);

        return ScrapConverter.toScrapResponseDTO(newScrap);
    }

    // 스크랩 취소하기
    public void deleteScrap(String userEmail, Long postId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Scrap scrap = scrapRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new RuntimeException("해당 user가 scrap한 post가 아닙니다."));

        scrapRepository.delete(scrap);
    }


    //내가 스크랩한 게시물 확인하기
    public Page<ScrapResponseDTO> findAll(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));


        Page<Scrap> scrapsByUser = scrapRepository.findAllByUser(user,pageable);
        return scrapsByUser.map(ScrapConverter::toScrapResponseDTO);

    }
}
