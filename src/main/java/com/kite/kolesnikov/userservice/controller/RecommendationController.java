package com.kite.kolesnikov.userservice.controller;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationUpdateDto;
import com.kite.kolesnikov.userservice.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendation controller")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Give the recommendation to the other user")
    public void giveRecommendation(@Valid @RequestBody RecommendationDto dto) {
        recommendationService.create(dto);
    }

    @PutMapping("/{recommendationId}")
    @Operation(summary = "Update given recommendation content")
    public void updateContent(@Valid @RequestBody RecommendationUpdateDto dto,
                              @PathVariable long recommendationId) {
        recommendationService.updateContent(dto, recommendationId);
    }

    @DeleteMapping("/{recommendationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete given recommendation")
    public void delete(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping("{recommendationId}")
    @Operation(summary = "Get recommendation")
    public RecommendationDto getRecommendation(@PathVariable long recommendationId) {
        return recommendationService.getRecommendation(recommendationId);
    }

    @GetMapping("/received")
    @Operation(summary = "Get all the received recommendations")
    public Page<RecommendationDto> getAllReceivedRecommendations(
            @RequestParam long userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        return recommendationService.getAllReceivedRecommendations(userId, pageNumber, pageSize);
    }

    @GetMapping("/given")
    @Operation(summary = "Get all the given recommendations")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @RequestParam long userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        return recommendationService.getAllGivenRecommendations(userId, pageNumber, pageSize);
    }
}
