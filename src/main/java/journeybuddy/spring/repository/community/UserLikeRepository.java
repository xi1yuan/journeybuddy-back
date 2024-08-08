package journeybuddy.spring.repository.community;

import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.community.UserLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    Page<UserLike> findAllByUser(User user, Pageable pageable);
    Optional<UserLike> findByUserAndPost(User user, Post post);

}
