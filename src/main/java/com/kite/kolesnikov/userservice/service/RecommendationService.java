package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationUpdateDto;
import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.dto.skill.UserSkillGuaranteeDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.exception.ResourceNotFoundException;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapper;
import com.kite.kolesnikov.userservice.repository.recommendation.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class RecommendationService {
    private final int recommendationIntervalMonths;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillService skillService;

    @Autowired
    public RecommendationService(@Value("${recommendation.interval.months}") int recommendationIntervalMonths,
                                 RecommendationRepository recommendationRepository,
                                 RecommendationMapper recommendationMapper,
                                 SkillService skillService) {

        this.recommendationIntervalMonths = recommendationIntervalMonths;
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
        this.skillService = skillService;
    }

    @Transactional
    public void create(RecommendationDto dto) {
        validateRecommendationInterval(dto);
        validateSkills(dto.getSkillOffers());

        Recommendation entity = recommendationMapper.toEntity(dto);
        Recommendation recommendation = recommendationRepository.save(entity);

        long recommendationId = recommendation.getId();
        processSkillOffers(dto, recommendationId);
        log.info("Recommendation: {} is created", recommendationId);
    }

    @Transactional
    public void updateContent(RecommendationUpdateDto dto, long recommendationId) {
        Recommendation recommendation = getRecommendationById(recommendationId);
        validateUserIsAuthor(recommendation, dto.getAuthorId());

        recommendation.setContent(dto.getContent());
        log.info("Recommendation: {} is updated", recommendationId);
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
        log.info("Recommendation: {} and its associated skills are deleted", recommendationId);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllReceivedRecommendations(long userId,
                                                                 int pageNumber,
                                                                 int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Recommendation> receiverRecommendations =
                recommendationRepository.findAllByReceiverId(userId, pageable);

        return receiverRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllGivenRecommendations(long authorId,
                                                              int pageNumber,
                                                              int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Recommendation> authorRecommendations =
                recommendationRepository.findAllByAuthorId(authorId, pageable);

        return authorRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Recommendation getRecommendationById(long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format(
                            "Recommendation: {0} does not exist", recommendationId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    /**
     * Processes skill offers associated with a recommendation. For each skill offer, it checks
     * if the receiver already has the skill and whether a guarantee exists. If not, it creates
     * a new skill guarantee; otherwise, it associates the skill offer with the recommendation.
     *
     * @param dto              The recommendation data containing skill offers.
     * @param recommendationId The ID of the recommendation to which skill offers are associated.
     */
    private void processSkillOffers(RecommendationDto dto, long recommendationId) {
        Set<SkillOfferDto> skillOffers = dto.getSkillOffers();
        if (skillOffers == null || skillOffers.isEmpty()) {
            return;
        }

        long receiverId = dto.getReceiverId();
        long authorId = dto.getAuthorId();
        for (SkillOfferDto skillOffer : skillOffers) {
            long skillId = skillOffer.getSkillId();

            if (skillService.userHaveSkill(skillId, receiverId) &&
                    !skillService.guaranteeExist(receiverId, skillId, authorId)) {

                skillService.saveSkillGuarantee(new UserSkillGuaranteeDto(receiverId, skillId, authorId));
            } else {
                skillOffer.setRecommendationId(recommendationId);
                skillService.saveSkillOffer(skillOffer);
            }
        }
    }

    private void validateRecommendationInterval(RecommendationDto dto) {
        long authorId = dto.getAuthorId();
        long receiverId = dto.getReceiverId();

        Recommendation lastRecommendation = recommendationRepository.findLastByAuthorAndReceiver(authorId, receiverId);
        if (lastRecommendation == null) {
            return;
        }

        LocalDateTime lastRecommendationDate = lastRecommendation.getCreatedAt();
        LocalDateTime currentDate = LocalDateTime.now();
        if (lastRecommendationDate.plusMonths(recommendationIntervalMonths).isAfter(currentDate)) {
            String errorMessage = MessageFormat.format(
                    "You've already recommended this user in the last {0} months",
                    recommendationIntervalMonths);

            log.error("User: {} already recommended User: {} in the last {} months",
                    authorId, receiverId, recommendationIntervalMonths);
            throw new DataValidationException(errorMessage);
        }
    }

    private void validateSkills(Set<SkillOfferDto> skillOffers) {
        if (skillOffers == null || skillOffers.isEmpty()) {
            return;
        }

        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        if (!skillService.skillsExistById(skillIds)) {
            throw new DataValidationException("Invalid skills");
        }
    }

    private void validateUserIsAuthor(Recommendation recommendation, long userId) {
        if (recommendation.getAuthor().getId() != userId) {
            log.error("User: {} is not the author of Recommendation: {}", userId, recommendation.getId());
            throw new DataValidationException("You can't change this recommendation");
        }
    }
}
