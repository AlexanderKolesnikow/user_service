package com.kite.kolesnikov.userservice.mapper;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffers", source = "skillOffers")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "skillOffers", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);
}
