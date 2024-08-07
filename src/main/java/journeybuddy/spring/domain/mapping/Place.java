package journeybuddy.spring.domain.mapping;


import jakarta.persistence.*;
import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.domain.Schedule;
import journeybuddy.spring.domain.Vote;
import journeybuddy.spring.domain.VoteOption;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Place { //원하는 장소 직접입력
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

}
