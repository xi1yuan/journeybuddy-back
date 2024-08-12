package journeybuddy.spring.service.community.post;

import jakarta.transaction.Transactional;
import journeybuddy.spring.converter.community.PostConverter;
import journeybuddy.spring.domain.community.Image;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.community.ImageRepository;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.community.post.PageContentResponse;
import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import journeybuddy.spring.web.dto.community.post.request.UpdatePostRequest;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostListResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ImageRepository imageRepository;

    @Override
    public List<PostListResponse> searchPosts(String keyword, String sortBy) {
        return null;
    }

    @Override
    public List<PostListResponse> getTop3Posts() {
        List<Post> posts = postRepository.findByOrderByLikeCountDesc(PageRequest.of(0, 3));

        List<PostListResponse> postListResponses = posts.stream()
                .map(PostConverter::toPostListResponse)
                .collect(Collectors.toList());

        return postListResponses;
    }

    @SneakyThrows
    @Override
    public PostDetailResponse createPost(CreatePostRequest request, List<MultipartFile> images, String userEmail){
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

        return PostConverter.toPostDetailResponse(savedPost);
    }

    @Override
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = getPost(postId);

        return PostConverter.toPostDetailResponse(post);
    }

    @SneakyThrows
    @Override
    public PostDetailResponse updatePost(Long postId, UpdatePostRequest request, List<MultipartFile> images, String userEmail) {
        User user = findMemberByEmail(userEmail);
        Post post = getPost(postId);

        // 해당 user가 작성한 게시글이 아닌 경우
        checkUserPost(post, user);

        // 삭제된 이미지 확인하고 DB 및 S3 에서 삭제
        deleteImage(request, post);

        // 새로 추가된 이미지 업로드
        List<String> imageUrls = new ArrayList<>();
        if(images != null) {
            imageUrls = uploadImages(images);
            log.info("새로 추가된 이미지: {}", imageUrls.get(0));
        }

        // 새로 추가된 이미지 DB에 저장
        List<Image> imageEntities = imageUrls.stream()
                .map(url -> Image.builder().url(url).post(post).build())
                .collect(Collectors.toList());

        // 게시글 수정
        post.update(request.getTitle(), request.getContent(), request.getLocation(), imageEntities);

        // 수정된 게시글 저장
        Post newPost = postRepository.save(post);

        return PostConverter.toPostDetailResponse(newPost);
    }

    private void deleteImage(UpdatePostRequest request, Post post) {
        if (request.getDeletedImageIds() == null) {
            return;
        }

        List<Image> deletedImages = post.getImages().stream()
                .filter(image -> request.getDeletedImageIds().contains(image.getId()))
                .collect(Collectors.toList());

        for (Image deletedImage : deletedImages) {
            log.info("이미지 삭제: {}", deletedImage.getUrl());
            s3ImageService.delete(deletedImage.getUrl()); // S3에서 이미지 삭제
            post.getImages().remove(deletedImage); // Post 객체에서 이미지 삭제
        }

        List<Long> deletedImageIds = deletedImages.stream()
                .map(Image::getId)
                .collect(Collectors.toList());

        if (!deletedImageIds.isEmpty()) {
            imageRepository.deleteAllByIdIn(deletedImageIds);  // imageRepository를 사용하여 이미지 엔티티 삭제
        }

        postRepository.save(post);  // Post 객체를 DB에 저장
    }


    private static void checkUserPost(Post post, User user) {
        if (!post.getUser().equals(user)) {
            log.info("해당 게시글을 삭제할 권한이 없음.");
            throw new IllegalArgumentException("해당 게시글을 삭제할 권한이 없습니다.");
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.info("게시글을 찾을 수 없음.");
            return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        });
    }

    @Override
    public void deletePost(Long postId, String userEmail) {
        User user = findMemberByEmail(userEmail);

        Post post = getPost(postId);

        postRepository.delete(post);
    }

    @Override
    public PageContentResponse<PostListResponse> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostListResponse> postListResponses = posts.getContent().stream()
                .map(PostConverter::toPostListResponse)
                .collect(Collectors.toList());

        return new PageContentResponse<>(
                postListResponses,
                posts.getTotalPages(),
                posts.getTotalElements(),
                posts.getSize()
        );
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
