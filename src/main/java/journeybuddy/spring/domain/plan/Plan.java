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


    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> vote;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Random random;

    @Builder
    public Plan(String name, LocalDate startDate, LocalDate endDate, String transport, List<Schedule> schedules, List<Vote> vote, User user, Random random) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.transport = transport;
        this.schedules = schedules;
        this.vote = vote;
        this.user = user;
        this.random = random;
    }
}
