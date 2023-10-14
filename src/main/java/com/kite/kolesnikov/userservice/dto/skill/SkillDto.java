package com.kite.kolesnikov.userservice.dto.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    @NotNull
    @Min(value = 4, message = "Skill title should be at least 4 characters long")
    @Max(value = 64, message = "Skill title should not be longer than 64 characters long")
    private String title;
}
