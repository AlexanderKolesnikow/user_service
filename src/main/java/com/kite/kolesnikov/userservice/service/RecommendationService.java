package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.recommendation.SkillOfferDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.entity.Skill;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.entity.user.UserSkillGuarantee;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapper;
import com.kite.kolesnikov.userservice.repository.UserSkillGuaranteeRepository;
import com.kite.kolesnikov.userservice.repository.recommendation.RecommendationRepository;
import com.kite.kolesnikov.userservice.repository.recommendation.SkillOfferRepository;
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
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillService skillService;
    private final UserService userService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateOnCreat(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        Recommendation save = recommendationRepository.save(recommendation);
//        processSkillOffers(save, recommendationDto.getSkillOffers());

        return recommendationMapper.toDto(recommendation);
    }


    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto, long recommendationId) {
        validateOnUpdate(recommendationDto, recommendationId);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllReceivedRecommendations(long receiverId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Recommendation> receiverRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        return receiverRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Recommendation> authorRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);

        return authorRecommendations.map(recommendationMapper::toDto);
    }

    private void processSkillOffers(Recommendation recommendation) {
        long userId = recommendation.getReceiver().getId();
        long authorId = recommendation.getAuthor().getId();
        List<Skill> userSkills = getUserSkills(userId);

        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            long skillId = skillOffer.getSkill().getId();

            if (userSkills.contains(skillOffer.getSkill()) && guaranteeNotExist(userId, skillId, authorId)) {
                saveUserSkillGuarantee(userId, skillId, authorId);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private List<Skill> getUserSkills(long userId) {
        User user = userService.getById(userId);
        return user.getSkills();
    }

    private void saveUserSkillGuarantee(long userId, long skillId, long guarantorId) {
        User user = userService.getById(userId);
        Skill skill = skillService.getById(skillId);
        User guarantor = userService.getById(guarantorId);

        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setUser(user);
        guarantee.setSkill(skill);
        guarantee.setGuarantor(guarantor);

        userSkillGuaranteeRepository.save(guarantee);
    }

    private boolean guaranteeNotExist(long userId, long skillId, long guarantorId) {
        return !userSkillGuaranteeRepository.existsByUserIdAndSkillIdAndGuarantorId(userId, skillId, guarantorId);
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

    private void validateOnUpdate(RecommendationDto recommendationDto, long recommendationId) {
        validateRecommendationExist(recommendationId);

        Set<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        if (skillOffers == null || skillOffers.isEmpty()) {
            validateSkillOffers(skillOffers);
        }
    }

    private void validateOnCreat(RecommendationDto recommendationDto) {
        validateLastUpdate(recommendationDto);

        Set<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        validateSkillOffers(skillOffers);
        validateSkillsExist(skillOffers);
    }

    private void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();
        Recommendation lastRecommendation = getLastRecommendation(authorId, receiverId);

        LocalDateTime lastUpdate = lastRecommendation.getUpdatedAt();
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
