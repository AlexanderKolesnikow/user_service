package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationGetDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationUpdateDto;
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
import java.util.List;
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
    public RecommendationDto create(RecommendationDto dto) {
        validateRecommendationInterval(dto);
        validateSkillsExist(dto.getSkillOffers());

        Recommendation recommendation = recommendationMapper.toEntity(dto);
        long recommendationId = recommendationRepository.save(recommendation).getId();
        processSkillOffers(dto, recommendationId);

        log.info("Recommendation: {} is created", recommendationId);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationUpdateDto dto, long recommendationId) {
        Recommendation recommendation = getRecommendationById(recommendationId);
        validateUserIsAuthor(recommendation, dto.getAuthorId());

        recommendation.setContent(dto.getContent());
        recommendationRepository.save(recommendation);

        log.info("Recommendation: {} is updated", recommendationId);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
        log.info("Recommendation: {} and its associated skills are deleted", recommendationId);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllReceivedRecommendations(RecommendationGetDto dto) {
        Pageable pageable = PageRequest.of(dto.getPageNumber(), dto.getPageSize());
        Page<Recommendation> receiverRecommendations =
                recommendationRepository.findAllByReceiverId(dto.getUserId(), pageable);

        return receiverRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllGivenRecommendations(RecommendationGetDto dto) {
        Pageable pageable = PageRequest.of(dto.getPageNumber(), dto.getPageSize());
        Page<Recommendation> authorRecommendations =
                recommendationRepository.findAllByAuthorId(dto.getUserId(), pageable);

        return authorRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Recommendation getRecommendationById(long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format(
                            "Recommendation: {0} does not exist", recommendationId);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }

    private void processSkillOffers(RecommendationDto recommendationDto, long recommendationId) {
        Set<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        long receiverId = recommendationDto.getReceiverId();
        long authorId = recommendationDto.getAuthorId();

        for (SkillOfferDto skillOffer : skillOffers) {
            long skillId = skillOffer.getSkillId();

            if (skillService.userHaveSkill(skillId, receiverId) && !skillService.guaranteeExist(receiverId, skillId, authorId)) {
                skillService.saveSkillGuarantee(new UserSkillGuaranteeDto(receiverId, skillId, authorId));
            } else {
                skillOffer.setRecommendationId(recommendationId);
                skillService.saveSkillOffer(skillOffer);
            }
        }
    }

    private void validateRecommendationInterval(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();
        Recommendation lastRecommendation = recommendationRepository.findLastByAuthorAndReceiver(authorId, receiverId);
        if (lastRecommendation == null) {
            return;
        }

        LocalDateTime lastRecommendationDate = lastRecommendation.getCreatedAt();
        LocalDateTime currentDate = LocalDateTime.now();
        if (lastRecommendationDate.plusMonths(RECOMMENDATION_INTERVAL_MONTHS).isAfter(currentDate)) {
            String errorMessage = MessageFormat.format(
                    "You've already recommended this user in the last {0} months",
                    RECOMMENDATION_INTERVAL_MONTHS);

            log.error("User: {} already recommended User: {} in the last {} months",
                    authorId, receiverId, RECOMMENDATION_INTERVAL_MONTHS);
            throw new DataValidationException(errorMessage);
        }
    }

    private void validateSkillsExist(Set<SkillOfferDto> skillOfferDtos) {
        List<Long> skillIds = skillOfferDtos.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        if (!skillService.skillsExistById(skillIds)) {
            throw new DataValidationException("Invalid skills");
        }
    }

    private void validateUserIsAuthor(Recommendation recommendation, long userId) {
        if (!(recommendation.getAuthor().getId() == userId)) {
            log.error("User: {} is not the author of Recommendation: {}", userId, recommendation.getId());
            throw new DataValidationException("You can't change this recommendation");
        }
    }
}
