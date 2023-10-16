package com.kite.kolesnikov.userservice.dto.recommendation;

import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Recommendation")
public class RecommendationDto {
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
    @NotBlank
    @Size(max = 4096, message = "Content can't be more than 4096 characters")
    private String content;
    private Set<SkillOfferDto> skillOffers;
}
