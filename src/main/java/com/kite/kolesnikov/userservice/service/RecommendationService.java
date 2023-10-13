package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationGetDto;
import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.dto.skill.UserSkillGuaranteeDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapper;
import com.kite.kolesnikov.userservice.repository.recommendation.RecommendationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int RECOMMENDATION_INTERVAL_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillService skillService;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateOnCreate(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        long recommendationId = recommendationRepository.save(recommendation).getId();
        processSkillOffers(recommendationDto, recommendationId);

        log.info("Recommendation {} is created", recommendationId);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto, long recommendationId) {
        validateOnUpdate(recommendationDto, recommendationId);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendationDto, recommendationId);

        log.info("Recommendation {} is updated", recommendationId);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
        log.info("Recommendation {} is deleted", recommendationId);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllReceivedRecommendations(RecommendationGetDto dto) {
        Pageable pageable = PageRequest.of(dto.getPageNumber(), dto.getPageSize());
        Page<Recommendation> receiverRecommendations = recommendationRepository.findAllByReceiverId(dto.getUserId(), pageable);

        return receiverRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllGivenRecommendations(RecommendationGetDto dto) {
        Pageable pageable = PageRequest.of(dto.getPageNumber(), dto.getPageSize());
        Page<Recommendation> authorRecommendations = recommendationRepository.findAllByAuthorId(dto.getUserId(), pageable);

        return authorRecommendations.map(recommendationMapper::toDto);
    }

    private void processSkillOffers(RecommendationDto recommendationDto, long recommendationId) {
        Set<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        long receiverId = recommendationDto.getReceiverId();
        long authorId = recommendationDto.getAuthorId();

        for (SkillOfferDto skillOffer : skillOffers) {
            long skillId = skillOffer.getSkillId();

            if (skillService.userHasSkill(skillId, receiverId) && !skillService.guaranteeExist(receiverId, skillId, authorId)) {
                skillService.saveSkillGuarantee(new UserSkillGuaranteeDto(receiverId, skillId, authorId));
            } else {
                skillOffer.setRecommendationId(recommendationId);
                skillService.saveSkillOffer(skillOffer);
            }
        }
    }

    private Recommendation getLastRecommendation(long authorId, long receiverId) {
        return recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format(
                            "User {0} hasn't given any recommendation to user {1}", authorId, receiverId);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }

    private void validateOnCreate(RecommendationDto recommendationDto) {
        validateLastUpdate(recommendationDto);

        Set<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        validateSkillOffers(skillOffers);
        validateSkillsExist(skillOffers);
    }

    private void validateOnUpdate(RecommendationDto recommendationDto, long recommendationId) {
        validateRecommendationExist(recommendationId);

        Set<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        if (skillOffers == null || skillOffers.isEmpty()) {
            validateSkillOffers(skillOffers);
        }
    }

    private void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();
        Recommendation lastRecommendation = getLastRecommendation(authorId, receiverId);

        LocalDateTime lastUpdate = lastRecommendation.getCreatedAt();
        LocalDateTime currentDate = LocalDateTime.now();

        if (lastUpdate.plusMonths(RECOMMENDATION_INTERVAL_MONTHS).isAfter(currentDate)) {
            String errorMessage = MessageFormat.format(
                    "You've already recommended the {0} user in the last {1} months",
                    receiverId, RECOMMENDATION_INTERVAL_MONTHS);
            log.error("User {} already recommended user {} in the last {} months",
                    authorId, receiverId, RECOMMENDATION_INTERVAL_MONTHS);
            throw new DataValidationException(errorMessage);
        }
    }

    private void validateSkillsExist(Set<SkillOfferDto> skillOffers) {
        for (SkillOfferDto skillOffer : skillOffers) {
            skillService.skillExistById(skillOffer.getSkillId());
        }
    }

    private void validateSkillOffers(Set<SkillOfferDto> skillOffers) {
        if (skillOffers == null || skillOffers.isEmpty()) {
            log.error("There was no skills offered");
            throw new DataValidationException("You should chose some skills to offer");
        }
    }

    private void validateRecommendationExist(long recommendationId) {
        if (!recommendationRepository.existsById(recommendationId)) {
            String errorMessage = MessageFormat.format("Recommendation ID: {0} does not exist", recommendationId);
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }
}
