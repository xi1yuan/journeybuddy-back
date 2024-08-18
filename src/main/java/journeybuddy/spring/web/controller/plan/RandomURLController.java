package journeybuddy.spring.web.controller.plan;

import io.swagger.v3.oas.annotations.Operation;
import journeybuddy.spring.service.plan.RandomURL.RandomURLServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/plan")
public class RandomURLController {

    private  final RandomURLServiceImpl randomURLService;

    //한 플랜당 url한개 만들 수 있음
    @Operation(summary = "랜덤 URL 생성", description = "랜덤 URL 생성")
    @GetMapping("/make_URL/{planId}")
    public String makeURL(@PathVariable Long planId, @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return randomURLService.createRandomURL(planId,email);
    }
}