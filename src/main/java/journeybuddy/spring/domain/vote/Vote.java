package journeybuddy.spring.domain.vote;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.common.BaseEntity;
import journeybuddy.spring.domain.plan.Plan;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //한 플랜에 여러개의 투표항목을 만들 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    //voteOption은 한 vote에 여러개가 존재함
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL)
    private List<VoteOption> votePlaceList = new ArrayList<>();




}
