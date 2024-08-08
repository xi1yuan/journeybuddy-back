package journeybuddy.spring.web.dto.community.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageContentResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;

    public PageContentResponse(List<T> content, int totalPages, long totalElements, int size) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
    }
}
