package journeybuddy.spring.web.controller.mypage;

import io.swagger.annotations.ApiOperation;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.community.PostConverter;
import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.repository.community.PostRepository;
import journeybuddy.spring.service.community.comment.CommentCommandService;
import journeybuddy.spring.service.community.like.UserLikeServiceImpl;
import journeybuddy.spring.service.community.post.PostCommandService;
import journeybuddy.spring.service.community.scrap.ScrapService;
import journeybuddy.spring.web.dto.community.comment.CommentResponseDTO;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my_page")
public class MyPageController {
    private final PostRepository postRepository;
    private final PostCommandService postCommandService;
    private final CommentCommandService commentCommandService;
    private final UserLikeServiceImpl userLikeServiceImpl;
    private final ScrapService scrapService;

    @GetMapping("/myLikes")
    public ApiResponse<Page<UserLikeResponesDTO>> findMyLikes(@AuthenticationPrincipal UserDetails userDetails,
                                                              Pageable pageable) {
        String userEmail = userDetails.getUsername();
        Page<UserLikeResponesDTO> likesPage = userLikeServiceImpl.findMyLike(userEmail,pageable);
        return ApiResponse.onSuccess(likesPage);
    }

    @GetMapping("/myScrap")
    public ApiResponse<Page<ScrapResponseDTO>> getMyScrap(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "9") int size) {

        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername();
        Page<ScrapResponseDTO> scrapPage = scrapService.findAll(userEmail,pageable);
        return ApiResponse.onSuccess(scrapPage);
    }

    //내가 쓴 게시글 상세조회(클릭시)
    @GetMapping("/posts/my_post/detail")
    @ApiOperation(value = "게시글 상세보기(클릭시)")
    public ApiResponse<?> checkMyPostDetail(@RequestParam Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        if (postId != null) {
            Post detailPost = postCommandService.checkPostDetail(postId, userDetails.getUsername());
            PostResponseDTO detailDTO = PostConverter.toPostResponseDTO(detailPost);
            return ApiResponse.onSuccess(detailDTO);
        } else {
            log.error("없는포스트");
            return ApiResponse.onFailure("COMMON404", "존재하지 않는포스트.", null);
        }
    }

    //게시글 삭제
    @DeleteMapping("/posts/delete/{postId}")
    @ApiOperation(value = "게시글 삭제", notes = "주어진 ID의 게시글을 삭제합니다.")
    public ApiResponse<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal  UserDetails userDetails) {
        if (postId != null) {
            postCommandService.deletePost(postId,userDetails.getUsername());
            log.info("Post with id {} deleted successfully", postId);
            return ApiResponse.onSuccess(null);
        } else {
            ApiResponse.onFailure("COMMON404", "사용자 정보가 없음", null);
        }
        return null;
    }

    //페이징 처리 되어있음
    @GetMapping("/my_posts/paging")
    @ApiOperation("내가 쓴 게시물 리스트 확인")
    public ApiResponse<Page<PostResponseDTO>> getMyPostsPage(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "9") int size,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername(); // JWT로 인증된 사용자의 이메일 가져오기
        log.info("게시글 조회 userId = {}", userEmail);
        Page<PostResponseDTO> myPost = postCommandService.getMyPeed(userEmail,pageable);
        return ApiResponse.onSuccess(myPost);
    }


    //페이징관련 팀원들이랑 상의하기
    @ApiOperation(value = "모든포스트페이징")
    @GetMapping("/checkAllPost")
    public ApiResponse<Page<PostResponseDTO>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "9") int size) {
        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAll(pageable);
        return ApiResponse.onSuccess(PostConverter.toDtoList(posts));
    }

    @GetMapping("/comment/myComment")
    public ApiResponse <Page<CommentResponseDTO>> checkMyComment(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "9") int size) {

        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername();
        log.info("나의 모든 댓글 조회");
        Page<CommentResponseDTO> checkMyComment = commentCommandService.checkMyComment(userEmail,pageable);
        return ApiResponse.onSuccess(checkMyComment);
    }
}
