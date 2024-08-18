package journeybuddy.spring.web.controller.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.community.post.PostService;
import journeybuddy.spring.web.dto.community.post.PageContentResponse;
import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import journeybuddy.spring.web.dto.community.post.request.UpdatePostRequest;
import journeybuddy.spring.web.dto.community.post.response.PostDetailResponse;
import journeybuddy.spring.web.dto.community.post.response.PostListResponse;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return ResponseEntity.ok(postService.getPostDetail(postId, pageable));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제 API")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "게시글 삭제 성공"));
    }

    @GetMapping("/list")
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회 API")
    public ResponseEntity<PageContentResponse<PostListResponse>> getAllPosts(
            @Parameter(description = "페이지 번호 / 원하는 페이지보다 -1 값으로 요청해주세요", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "정렬 기준 / \"createdDateTime\" , \"likeCount\" 둘 중 하나로 요청해주세요 ", example = "createdDateTime")
            @RequestParam(value = "sort", defaultValue = "createdDateTime") String sort) {

        Pageable pageable = PageRequest.of(page, 12, Sort.by(sort).descending());

        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/top3")
    @Operation(summary = "좋아요 기준 인기 게시글 조회", description = "인기 게시글 조회 API")
    public ResponseEntity<List<PostListResponse>> getTop3Posts() {
        return ResponseEntity.ok(postService.getTop3Posts());
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 수정 API")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Parameter(description = "게시글 수정 요청 정보", example = "{'title':'수정된 제목', 'content':'수정된 내용', 'location':'수정된 위치', 'deletedImageIds':[1, 2]}")
            @RequestPart("request") UpdatePostRequest request,
            @Parameter(description = "게시글 수정 이미지 리스트", example = "image.jpg")
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(postId, request, images, userDetails.getUsername()));
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "게시글 검색 API")
    public ResponseEntity<List<PostListResponse>> searchPosts(
            @Parameter(description = "검색어", example = "여행")
            @RequestParam(value = "location", defaultValue = "") String location,
            @Parameter(description = "페이지 번호 / 원하는 페이지보다 -1 값으로 요청해주세요", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "정렬 기준 / \"createdDateTime\" , \"likeCount\" 둘 중 하나로 요청해주세요 ", example = "createdDateTime")
            @RequestParam(value = "sort", defaultValue = "createdDateTime") String sort) {

        Pageable pageable = PageRequest.of(page, 12, Sort.by(sort).descending());
        return ResponseEntity.ok(postService.searchPosts(location, sort, pageable));
    }

}
