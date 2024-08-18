package journeybuddy.spring.web.dto.plan.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class ProvinceResponse {
    private String code;
    private String name;
}
