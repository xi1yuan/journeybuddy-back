package journeybuddy.spring.web.controller.community;

import journeybuddy.spring.service.community.post.PostService;
import journeybuddy.spring.web.dto.community.post.request.CreatePostRequest;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(value="/save", consumes = {"multipart/form-data"})
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
}
