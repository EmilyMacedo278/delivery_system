package fiap.com.br.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewRankingResponse {

    private Long dishId;
    private String dishName;
    private double average;
    private long count;
}