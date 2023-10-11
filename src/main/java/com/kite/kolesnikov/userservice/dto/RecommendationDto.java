package com.kite.kolesnikov.userservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    private Long authorId;
    private Long receiverId;
    @NotNull
    @NotEmpty
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
