package journeybuddy.spring.repository.vote;

import journeybuddy.spring.domain.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUser_Email(String email);

    @Query("SELECT COUNT(v) > 0 FROM Vote v WHERE v.user.id = :userId AND v.id = :voteId")
    boolean checkVoteDuplication(@Param("userId") Long userId, @Param("voteId") Long voteId);
}