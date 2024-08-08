package journeybuddy.spring.web.dto.community.post;


import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

}
