package com.kite.kolesnikov.userservice.controller;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationGetDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationUpdateDto;
import com.kite.kolesnikov.userservice.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto create(@Valid @RequestBody RecommendationDto dto) {
        return recommendationService.create(dto);
    }

    @PutMapping("/{recommendationId}")
    public RecommendationDto update(@Valid @RequestBody RecommendationUpdateDto dto,
                                    @PathVariable long recommendationId) {
        return recommendationService.update(dto, recommendationId);
    }

    @DeleteMapping("/{recommendationId}")
    public void delete(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping("/received")
    public Page<RecommendationDto> getAllReceivedRecommendations(
            @RequestParam long userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        RecommendationGetDto dto = new RecommendationGetDto(userId, pageNumber, pageSize);
        return recommendationService.getAllReceivedRecommendations(dto);
    }

    @GetMapping("/given")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @RequestParam long userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        RecommendationGetDto dto = new RecommendationGetDto(userId, pageNumber, pageSize);
        return recommendationService.getAllGivenRecommendations(dto);
    }
}
