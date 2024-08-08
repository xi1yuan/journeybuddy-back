package journeybuddy.spring.service.community.post;

import jakarta.transaction.Transactional;
import journeybuddy.spring.converter.community.PostConverter;
import journeybuddy.spring.domain.community.Image;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import journeybuddy.spring.web.dto.community.post.request.UpdatePostRequest;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;
    private final PostRepository postRepository;

    @Override
    public List<PostResponse> searchPosts(String keyword, String sortBy) {
        return null;
    }

    @Override
    public List<PostResponse> getTop3Posts() {
        return null;
    }

    @SneakyThrows
    @Override
    public PostResponse createPost(CreatePostRequest request, List<MultipartFile> images, String userEmail){
        List<String> imageUrls = uploadImages(images);
        User user = findMemberByEmail(userEmail);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .createdDateTime(LocalDateTime.now())
                .likeCount(0)
                .commentCount(0)
                .user(user)
                .build();

        List<Image> imageEntities = imageUrls.stream()
                .map(url -> Image.builder().url(url).post(post).build())
                .collect(Collectors.toList());

        post.setImages(imageEntities);



        Post savedPost = postRepository.save(post);

        return PostConverter.toPostResponse(savedPost);
    }

    @Override
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.info("게시글을 찾을 수 없음.");
            return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        });

        return PostConverter.toPostDetailResponse(post);
    }

    @Override
    public PostResponse updatePost(Long postId, UpdatePostRequest request, String userEmail) {
        return null;
    }

    @Override
    public void deletePost(Long postId) {

    }

    @Override
    public List<PostResponse> getAllPosts() {
        return null;
    }

    private List<String> uploadImages(List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = s3ImageService.upload(image, "post");
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    private User findMemberByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.info("회원 정보를 찾을 수 없음.");
            return new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        });
    }
}
