package journeybuddy.spring.web.dto.community.comment;


import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentRequestDTO {
    private String comment;
}
