package com.kite.kolesnikov.userservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationEventDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<RecommendationEventDto> {
    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper objectMapper,
                                        @Value(value = "${spring.data.redis.channels.recommendation_chanel.name}") String chanelName,
                                        RecommendationMapper recommendationMapper) {
        super(redisTemplate, objectMapper, chanelName);
        this.recommendationMapper = recommendationMapper;
    }

    public void publish(Recommendation recommendation) {
        RecommendationEventDto eventDto = recommendationMapper.toEventDto(recommendation);
        publishInChanel(eventDto);
    }
}
