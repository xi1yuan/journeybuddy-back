package journeybuddy.spring.web.controller;


import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.ScrapConverter;
import journeybuddy.spring.domain.mapping.Scrap;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.ScrapRepository;
import journeybuddy.spring.service.ScrapService.ScrapCommandService;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapRequestDTO;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my_page/scraps")
public class ScrapRestController {
    private final ScrapRepository scrapRepository;
    private final ScrapCommandService scrapCommandService;
    private final PostRepository postRepository;


    //convert로직 service로 이동시키기
    @PostMapping("/save")
    public ApiResponse<Scrap> save(@RequestBody ScrapRequestDTO scrapRequestDTO,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Scrap savedScrap = ScrapConverter.toScrap(scrapRequestDTO);
        String userEmail = userDetails.getUsername();
        Long postId = scrapRequestDTO.getPostId();
        savedScrap = scrapCommandService.saveScrap(userEmail,postId,savedScrap);
        return ApiResponse.onSuccess(savedScrap);
    }

    @GetMapping("/myScrap")
    public ApiResponse<Page<ScrapResponseDTO>> getMyScrap(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "9") int size) {

        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = userDetails.getUsername();
        Page<ScrapResponseDTO> scrapPage = scrapCommandService.findAll(userEmail,pageable);
        return ApiResponse.onSuccess(scrapPage);
    }
}
