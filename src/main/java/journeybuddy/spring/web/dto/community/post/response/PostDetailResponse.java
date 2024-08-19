package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostDetailResponse {
    private Long postId;
    private String title;
    private String content;
    private String location;
    private String writer;
    private String createdAt;
    private List<ImageDTO> imageUrlList;
    private int likeCount;
    private int commentCount;
    private List<CommentWriterDTO> commentWriterList;
    private List<CommentDTO> commentList;
    private int totalPages;
    private long totalComments;

    @Builder
    public PostDetailResponse(Long postId, String title, String content, String location, String writer, String createdAt, List<ImageDTO> imageUrlList, int likeCount, int commentCount, List<CommentWriterDTO> commentWriterList, List<CommentDTO> commentList, int totalPages, long totalComments) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.writer = writer;
        this.createdAt = createdAt;
        this.imageUrlList = imageUrlList;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.commentWriterList = commentWriterList;
        this.commentList = commentList;
        this.totalPages = totalPages;
        this.totalComments = totalComments;
    }
}
