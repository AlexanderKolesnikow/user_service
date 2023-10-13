package com.kite.kolesnikov.userservice.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationGetDto {
    @NotNull
    private Long userId;
    @NotNull
    private Integer pageNumber;
    @NotNull
    private Integer pageSize;
}
