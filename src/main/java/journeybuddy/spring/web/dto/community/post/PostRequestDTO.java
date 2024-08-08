package journeybuddy.spring.web.dto.community.post;

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
