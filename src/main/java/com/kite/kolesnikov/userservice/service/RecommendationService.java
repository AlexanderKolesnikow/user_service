package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.SkillOfferDto;
import com.kite.kolesnikov.userservice.entity.Skill;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.entity.user.UserSkillGuarantee;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapper;
import com.kite.kolesnikov.userservice.repository.UserSkillGuaranteeRepository;
import com.kite.kolesnikov.userservice.repository.recommendation.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int RECOMMENDATION_INTERVAL_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferService skillOfferService;
    private final SkillService skillService;
    private final UserService userService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto, long recommendationId) {
        validateRecommendationToUpdate(recommendationId);
        validate(recommendationDto);

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
    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, int pageNumber, int pageSize) {
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
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> userSkills = getUserSkills(userId);

        for (SkillOffer skillOffer : skillOffers) {
            long skillId = skillOffer.getSkill().getId();

            if (userSkills.contains(skillOffer.getSkill()) && guaranteeNotExist(userId, skillId, authorId)) {
                saveUserSkillGuarantee(userId, skillId, authorId);
            } else {
                skillOfferService.save(skillOffer);
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
                .orElseThrow(() -> new DataValidationException("Invalid recommendation"));
    }

    private void validate(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skills = recommendationDto.getSkillOffers();

        validateLastUpdate(recommendationDto);
        validateSkillsListNotEmptyOrNull(skills);
        validateSkillsAreInRepository(skills);
    }

    private void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long userId = recommendationDto.getReceiverId();

        Optional<Recommendation> lastRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime currentDate = LocalDateTime.now();

            if (lastUpdate.plusMonths(RECOMMENDATION_INTERVAL_MONTHS).isAfter(currentDate)) {
                String errorMessage = String.format(
                        "You've already recommended the %d user in the last %d months",
                        userId, RECOMMENDATION_INTERVAL_MONTHS);

                throw new DataValidationException(errorMessage);
            }
        }
    }

    private void validateSkillsListNotEmptyOrNull(List<SkillOfferDto> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new DataValidationException("You should choose some skills");
        }
    }

    private void validateSkillsAreInRepository(List<SkillOfferDto> skills) {
        List<Long> skillIds = getUniqueSkillIds(skills);

        for (Long skillId : skillIds) {

            if (!skillService.existsById(skillId)) {
                throw new DataValidationException("Invalid skills");
            }
        }
    }

    private List<Long> getUniqueSkillIds(List<SkillOfferDto> skills) {
        return skills.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();
    }

    private void validateRecommendationToUpdate(long recommendationId) {
        recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Invalid recommendation to update"));
    }
}
