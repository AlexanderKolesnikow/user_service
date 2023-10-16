package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.dto.skill.UserSkillGuaranteeDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapperImpl;
import com.kite.kolesnikov.userservice.mapper.SkillOfferMapperImpl;
import com.kite.kolesnikov.userservice.repository.recommendation.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Mock
    private SkillService skillService;
    private RecommendationService recommendationService;

    private SkillOfferDto skillOffer1;
    private SkillOfferDto skillOffer2;
    private Set<SkillOfferDto> skillOffers;
    private RecommendationDto recommendationDto;
    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(3,
                recommendationRepository,
                recommendationMapper,
                skillService);

        skillOffer1 = SkillOfferDto.builder().skillId(1L).build();
        skillOffer2 = SkillOfferDto.builder().skillId(2L).build();

        skillOffers = new HashSet<>();
        skillOffers.add(skillOffer1);
        skillOffers.add(skillOffer2);

        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("test")
                .build();

        recommendation = Recommendation.builder()
                .id(1L)
                .author(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .content("test")
                .build();
    }

    @Test
    void testCreate_Successful_RecommendationWithoutSkills() {
        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testCreate_Successful_RecommendationWithSkillsToOffer() {
        recommendationDto.setSkillOffers(skillOffers);

        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(skillService.skillsExistById(anyList())).thenReturn(true);
        when(skillService.userHaveSkill(anyLong(), anyLong())).thenReturn(false);

        recommendationService.create(recommendationDto);

        verify(skillService).saveSkillOffer(skillOffer1);
        verify(skillService).saveSkillOffer(skillOffer2);
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testCreate_Successful_RecommendationWithSkillsToGuarantee() {
        recommendationDto.setSkillOffers(skillOffers);

        UserSkillGuaranteeDto skillGuarantee1 = new UserSkillGuaranteeDto(
                recommendationDto.getReceiverId(), skillOffer1.getSkillId(), recommendationDto.getAuthorId());
        UserSkillGuaranteeDto skillGuarantee2 = new UserSkillGuaranteeDto(
                recommendationDto.getReceiverId(), skillOffer2.getSkillId(), recommendationDto.getAuthorId());

        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(skillService.skillsExistById(anyList())).thenReturn(true);
        when(skillService.userHaveSkill(anyLong(), anyLong())).thenReturn(true);
        when(skillService.guaranteeExist(anyLong(), anyLong(), anyLong())).thenReturn(false);

        recommendationService.create(recommendationDto);

        verify(skillService).saveSkillGuarantee(skillGuarantee1);
        verify(skillService).saveSkillGuarantee(skillGuarantee2);
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }


}
