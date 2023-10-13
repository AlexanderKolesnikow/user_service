package com.kite.kolesnikov.userservice.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    @NotNull
    private Long skillId;
    private Long recommendationId;
}
