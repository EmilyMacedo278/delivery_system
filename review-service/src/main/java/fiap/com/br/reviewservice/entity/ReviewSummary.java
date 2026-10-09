package fiap.com.br.reviewservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_summaries")
public class ReviewSummary {

    @Id
    private Long dishId;

    private String dishName;

    private long ratingSum;

    private long count;
}