package com.kite.kolesnikov.userservice.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillGuaranteeDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long skillId;
    @NotNull
    private Long guarantorId;
}
