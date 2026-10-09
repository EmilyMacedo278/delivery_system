package fiap.com.br.reviewservice.service;

import fiap.com.br.reviewservice.config.RabbitConfig;
import fiap.com.br.reviewservice.dto.ReviewAccumulator;
import fiap.com.br.reviewservice.dto.ReviewMessage;
import fiap.com.br.reviewservice.dto.ReviewRankingResponse;
import fiap.com.br.reviewservice.entity.ReviewSummary;
import fiap.com.br.reviewservice.repository.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewSummaryRepository repository;

    private final ConcurrentHashMap<Long, ReviewAccumulator> buffer =
            new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receiveReview(ReviewMessage message) {

        buffer.merge(
                message.getDishId(),

                new ReviewAccumulator(
                        message.getDishName(),
                        message.getRating(),
                        1
                ),

                (current, incoming) ->
                        new ReviewAccumulator(
                                current.getDishName(),
                                current.getRatingSum()
                                        + incoming.getRatingSum(),
                                current.getCount()
                                        + incoming.getCount()
                        )
        );

        System.out.println(
                "Review received for: "
                        + message.getDishName()
        );
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flushBuffer() {

        if (buffer.isEmpty()) {
            return;
        }

        for (Long dishId : buffer.keySet()) {

            ReviewAccumulator accumulated =
                    buffer.remove(dishId);

            if (accumulated == null) {
                continue;
            }

            ReviewSummary summary =
                    repository.findById(dishId)
                            .orElse(
                                    new ReviewSummary(
                                            dishId,
                                            accumulated.getDishName(),
                                            0,
                                            0
                                    )
                            );

            summary.setDishName(
                    accumulated.getDishName()
            );

            summary.setRatingSum(
                    summary.getRatingSum()
                            + accumulated.getRatingSum()
            );

            summary.setCount(
                    summary.getCount()
                            + accumulated.getCount()
            );

            repository.save(summary);

            System.out.println(
                    "Review buffer flushed for dish: "
                            + dishId
            );
        }
    }

    public List<ReviewRankingResponse> getRanking() {

        return repository.findAll()
                .stream()
                .map(summary -> {

                    double average =
                            summary.getCount() == 0
                                    ? 0
                                    : (double) summary.getRatingSum()
                                      / summary.getCount();

                    return new ReviewRankingResponse(
                            summary.getDishId(),
                            summary.getDishName(),
                            average,
                            summary.getCount()
                    );
                })
                .sorted(
                        Comparator.comparingDouble(
                                ReviewRankingResponse::getAverage
                        ).reversed()
                )
                .toList();
    }
}