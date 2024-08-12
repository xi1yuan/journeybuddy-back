package journeybuddy.spring.domain.community;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String location;

    private int likeCount;

    private int commentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<UserLike> userLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Scrap> scrapList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> images;


    @Builder
    public Post(String title, String location, String content, int likeCount, int commentCount, List<Image> images, User user) {
        this.title = title;
        this.location = location;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.images = images;
        this.user = user;
    }

    public void setImages(List<Image> images) {
        this.images = images;
        for (Image image : images) {
            image.setPost(this);
        }
    }

    public void update(String title, String content, String location, List<Image> imageEntities) {
        this.title = title;
        this.content = content;
        this.location = location;
        if (imageEntities != null && !imageEntities.isEmpty()) {
            this.images.addAll(imageEntities);  // 기존 이미지에 새로운 이미지들을 추가
            for (Image image : imageEntities) {
                image.setPost(this);  // 새로운 이미지와 게시글 간의 관계를 설정
            }
        }
    }

    public void setLikeCount(int i) {
        this.likeCount = i;
    }

    public void setCommentCount(int i) {
        this.commentCount = i;
    }
}
