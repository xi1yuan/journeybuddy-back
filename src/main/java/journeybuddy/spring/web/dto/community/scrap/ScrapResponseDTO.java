package journeybuddy.spring.web.dto.community.scrap;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapResponseDTO {
    private Long id;
    private Long postId;
    private Long userId;
}
