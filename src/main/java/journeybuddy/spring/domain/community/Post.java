package journeybuddy.spring.domain.community;


import jakarta.persistence.*;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.common.BaseEntity;
import lombok.*;

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

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

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
    public Post(String title, String location, String content, LocalDateTime createdDateTime, int likeCount, int commentCount, List<Image> images, User user) {
        this.title = title;
        this.location = location;
        this.content = content;
        this.createdDateTime = createdDateTime;
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
}
