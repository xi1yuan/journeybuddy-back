package journeybuddy.spring.domain;

import jakarta.persistence.*;
import journeybuddy.spring.domain.mapping.Place;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VoteOption {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    //각 항목에 대한 투표 수
    @Column(nullable = false)
    private int voteCount;

    @Column(nullable = false)
    private String optionText;


}
