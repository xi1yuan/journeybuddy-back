package journeybuddy.spring.web.dto.user;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    private String token;
}
