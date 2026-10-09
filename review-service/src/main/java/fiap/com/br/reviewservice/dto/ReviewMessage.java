package fiap.com.br.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMessage {

    private Long dishId;
    private String dishName;
    private int rating;
    private String comment;
}