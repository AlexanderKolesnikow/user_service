package com.kite.kolesnikov.userservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
    @NotBlank
    @Size(max = 4096, message = "Content can't be more than 4096 characters")
    private String content;
    @Valid
    @NotEmpty(message = "You should chose some skills")
    private Set<SkillOfferDto> skillOffers;
    private ZonedDateTime createdAt;
}
