package journeybuddy.spring.web.controller.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.community.post.PostService;
import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Community API", description = "커뮤니티 관련 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(value="/save", consumes = {"multipart/form-data"})
    @Operation(summary = "게시글 작성", description = "게시글 작성 API")
    public ResponseEntity<PostResponse> savePost(
            @RequestPart("request") CreatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        // null 체크 및 빈 리스트 초기화
        if (images == null) {
            images = new ArrayList<>();
        }

        return ResponseEntity.ok(postService.createPost(request, images, userDetails.getUsername()));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 조회 API")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }
}
