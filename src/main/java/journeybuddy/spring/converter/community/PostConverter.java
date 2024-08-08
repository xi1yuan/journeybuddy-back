package journeybuddy.spring.converter.community;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.web.dto.community.post.PostRequestDTO;
import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostListResponse;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public class PostConverter {

    public static PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .userName(post.getUser().getNickname())
                .userId(post.getUser().getId())
                .imageUrlList(post.getImages().stream().map(image -> image.getUrl()).collect(Collectors.toList()))
                .build();
    }

    public static PostDetailResponse toPostDetailResponse(Post post) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .writer(post.getUser().getNickname())
                .createdAt(post.getCreatedDateTime().toString())
                .imageUrlList(post.getImages().stream().map(image -> image.getUrl()).collect(Collectors.toList()))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();
    }

    public static PostListResponse toPostListResponse(Post post) {
        return PostListResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerName(post.getUser().getNickname())
                .createdDate(post.getCreatedDateTime())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .location(post.getLocation())
                .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getUrl())
                .build();
    }




        public static Post toPost(PostRequestDTO postRequestDTO) {  //Post 엔티티에 저장
            return Post.builder()
                   // .id(postRequestDTO.getId())
                    .title(postRequestDTO.getTitle())
                    .content(postRequestDTO.getContent())
                    .build();

        }

        public static PostRequestDTO toPostRequestDTO(Post post) {  //post에서 받아옴 PostRequestDTO로
            return PostRequestDTO.builder()
                  //  .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .build();

        }

        public static PostResponseDTO toPostResponseDTO(Post post) {  //post에서 받아옴 PostResponseDTO로
            return PostResponseDTO.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .title(post.getTitle())
                    .userId(post.getUser().getId())
                    .build();
        }

        /* Page<Entity> -> Page<Dto> 변환처리 */
        public static Page<PostResponseDTO> toDtoList(Page<Post> posts) {
            return posts.map(post -> {
                //null확인안하면 exception발생
                Long userId = (post.getUser() != null) ? post.getUser().getId() : null;

                return PostResponseDTO.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .userId(userId)
                        .build();
            });
        }

    }