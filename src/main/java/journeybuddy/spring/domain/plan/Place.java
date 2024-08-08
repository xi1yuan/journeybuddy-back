package journeybuddy.spring.domain.plan;


import jakarta.persistence.*;
import lombok.*;

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
