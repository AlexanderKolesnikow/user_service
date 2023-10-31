package com.kite.kolesnikov.userservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite.kolesnikov.userservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String chanelName;

    protected void publishInChanel(T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event of type {}: {}", event.getClass().getName(), e.getMessage());
            throw new JsonSerializationException(event);
        }
        redisTemplate.convertAndSend(chanelName, json);
        log.info("Successfully published event of type {} to channel {}", event.getClass().getSimpleName(), chanelName);
    }
}
