package journeybuddy.spring.domain;

import jakarta.persistence.*;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VoteRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vote_option_id")
    private VoteOption voteOption;
}
