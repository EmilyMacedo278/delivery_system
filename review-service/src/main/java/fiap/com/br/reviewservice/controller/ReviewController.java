package fiap.com.br.reviewservice.controller;

import fiap.com.br.reviewservice.dto.ReviewRankingResponse;
import fiap.com.br.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @GetMapping("/ranking")
    public List<ReviewRankingResponse> ranking() {
        return service.getRanking();
    }
}