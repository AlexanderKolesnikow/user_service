package com.kite.kolesnikov.userservice.mapper;

import com.kite.kolesnikov.userservice.dto.RecommendationDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    RecommendationDto toDto(Recommendation recommendation);

    Recommendation toEntity(RecommendationDto recommendationDto);
}
