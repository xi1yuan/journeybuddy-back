package journeybuddy.spring.repository.community;

import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    List<Post> findPostsByUserEmail(String email);
    Page<Post> findAllByUser(User user, Pageable pageable);

    List<Post> findByOrderByLikeCountDesc(PageRequest of);
}
