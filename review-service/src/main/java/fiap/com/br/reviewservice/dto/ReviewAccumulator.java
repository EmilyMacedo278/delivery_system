package fiap.com.br.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewAccumulator {

    private String dishName;
    private long ratingSum;
    private long count;
}