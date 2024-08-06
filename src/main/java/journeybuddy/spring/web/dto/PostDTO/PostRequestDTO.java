package journeybuddy.spring.web.dto.PostDTO;

import journeybuddy.spring.domain.Post;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostRequestDTO {

    private String title;
    private String content;

}
