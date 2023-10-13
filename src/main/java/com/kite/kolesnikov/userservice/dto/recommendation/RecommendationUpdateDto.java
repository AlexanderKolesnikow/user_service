package com.kite.kolesnikov.userservice.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationUpdateDto {
    @NotNull
    private Long authorId;
    @NotBlank
    @Size(min = 4, max = 4096, message = "Content can't be more than 4096 characters")
    private String content;
}
