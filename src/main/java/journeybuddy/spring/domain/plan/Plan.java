package journeybuddy.spring.domain.plan;


import jakarta.persistence.*;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.vote.Vote;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Plan extends BaseEntity {

    @Id
    @Column(name="plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String transport;

    @Column
    private String preference;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    //한 플랜에 여러개의 투표를 만들 수 있음.
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
