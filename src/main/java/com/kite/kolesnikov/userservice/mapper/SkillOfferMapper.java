package com.kite.kolesnikov.userservice.mapper;

import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(target = "skill.id", source = "skillId")
    @Mapping(target = "recommendation.id", source = "recommendationId")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);
}
