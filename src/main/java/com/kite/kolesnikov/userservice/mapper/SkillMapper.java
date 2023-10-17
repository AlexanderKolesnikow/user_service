package com.kite.kolesnikov.userservice.mapper;

import com.kite.kolesnikov.userservice.dto.skill.SkillDto;
import com.kite.kolesnikov.userservice.entity.Skill;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toEntity(SkillDto SkillDto);

    SkillDto toDto(Skill skill);
}
