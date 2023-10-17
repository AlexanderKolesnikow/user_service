package com.kite.kolesnikov.userservice.mapper;

import com.kite.kolesnikov.userservice.dto.skill.UserSkillGuaranteeDto;
import com.kite.kolesnikov.userservice.entity.user.UserSkillGuarantee;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "skill.id", source = "skillId")
    @Mapping(target = "guarantor.id", source = "guarantorId")
    UserSkillGuarantee toEntity(UserSkillGuaranteeDto userSkillGuaranteeDto);
}
