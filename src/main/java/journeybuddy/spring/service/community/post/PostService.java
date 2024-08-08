package journeybuddy.spring.service.community.post;

import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import journeybuddy.spring.web.dto.community.post.request.UpdatePostRequest;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<PostResponse> searchPosts(String keyword, String sortBy);
    List<PostResponse> getTop3Posts();
    PostResponse createPost(CreatePostRequest request, List<MultipartFile> images, String userEmail);
    PostDetailResponse getPostDetail(Long postId);
    PostResponse updatePost(Long postId, UpdatePostRequest request, String userEmail);
    void deletePost(Long postId, String userEmail);
    List<PostResponse> getAllPosts();
}
