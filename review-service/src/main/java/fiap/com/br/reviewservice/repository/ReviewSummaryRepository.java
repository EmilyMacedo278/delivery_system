package fiap.com.br.reviewservice.repository;

import fiap.com.br.reviewservice.entity.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewSummaryRepository
        extends JpaRepository<ReviewSummary, Long> {
}