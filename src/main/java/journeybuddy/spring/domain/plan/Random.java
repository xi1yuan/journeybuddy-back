package journeybuddy.spring.domain.plan;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Random {

    //하나의 플랜에대해 하나의 url생성가능

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String URL;


    @OneToOne
    @JoinColumn(name = "plan_id")  // 외래키를 정의
    private Plan plan;

}