package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationDto;
import com.kite.kolesnikov.userservice.dto.recommendation.RecommendationUpdateDto;
import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.dto.skill.UserSkillGuaranteeDto;
import com.kite.kolesnikov.userservice.entity.recommendation.Recommendation;
import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.exception.ResourceNotFoundException;
import com.kite.kolesnikov.userservice.mapper.RecommendationMapperImpl;
import com.kite.kolesnikov.userservice.repository.recommendation.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillService skillService;
    @Spy
    private RecommendationMapperImpl recommendationMapper;
    private RecommendationService recommendationService;

    private SkillOfferDto skillOffer1;
    private SkillOfferDto skillOffer2;
    private Set<SkillOfferDto> skillOffers;
    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private RecommendationUpdateDto recommendationUpdateDto;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(6,
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

        recommendationUpdateDto = RecommendationUpdateDto.builder()
                .authorId(1L)
                .content("Hello!")
                .build();
    }

    @Test
    void testCreate_Successful_FirstGivenRecommendation() {
        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(skillService, never()).saveSkillGuarantee(any(UserSkillGuaranteeDto.class));
        verify(skillService, never()).saveSkillOffer(any(SkillOfferDto.class));
    }

    @Test
    void testCreate_Successful_PassesTimeIntervalValidation() {
        Recommendation recommendation1 = new Recommendation();
        recommendation1.setCreatedAt(LocalDateTime.of(2023, 1, 1, 11, 11));

        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(recommendation1);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        verify(skillService, never()).saveSkillGuarantee(any(UserSkillGuaranteeDto.class));
        verify(skillService, never()).saveSkillOffer(any(SkillOfferDto.class));
    }

    @Test
    void testCreate_Successful_RecommendationWithSkillsToOffer() {
        recommendationDto.setSkillOffers(skillOffers);

        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(skillService.skillsExistById(anySet())).thenReturn(true);
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
        when(skillService.skillsExistById(anySet())).thenReturn(true);
        when(skillService.userHaveSkill(anyLong(), anyLong())).thenReturn(true);
        when(skillService.guaranteeExist(anyLong(), anyLong(), anyLong())).thenReturn(false);

        recommendationService.create(recommendationDto);

        verify(skillService).saveSkillGuarantee(skillGuarantee1);
        verify(skillService).saveSkillGuarantee(skillGuarantee2);
        verify(recommendationRepository).save(any(Recommendation.class));
    }

    @Test
    void testCreate_DidntPassTimeIntervalValidation() {
        recommendation.setCreatedAt(LocalDateTime.now());

        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(recommendation);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void testCreate_DidntPassSkillValidation() {
        recommendationDto.setSkillOffers(skillOffers);

        when(recommendationRepository.findLastByAuthorAndReceiver(anyLong(), anyLong())).thenReturn(null);
        when(skillService.skillsExistById(anySet())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void testUpdateContent_Successful() {
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recommendation));

        recommendationService.updateContent(recommendationUpdateDto, 1L);

        assertEquals(recommendationUpdateDto.getContent(), recommendation.getContent());
    }

    @Test
    void testUpdateContent_UserIsNotAuthor() {
        recommendationUpdateDto.setAuthorId(2L);

        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recommendation));

        assertThrows(DataValidationException.class,
                () -> recommendationService.updateContent(recommendationUpdateDto, 1L));
    }

    @Test
    void testUpdateContent_RecommendationNotExist() {
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recommendationService.updateContent(recommendationUpdateDto, 1L));
    }

    @Test
    void testGetAllReceivedRecommendations() {
        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        Pageable pageable = PageRequest.of(0, 5);

        when(recommendationRepository.findAllByReceiverId(2L, pageable)).thenReturn(page);

        Page<RecommendationDto> result = recommendationService.getAllReceivedRecommendations(2L, 0, 5);

        RecommendationDto recommendationDto1 = result.getContent().get(0);
        assertEquals(recommendationDto, recommendationDto1);
    }

    @Test
    void testGetAllGivenRecommendations() {
        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        Pageable pageable = PageRequest.of(0, 5);

        when(recommendationRepository.findAllByAuthorId(1L, pageable)).thenReturn(page);

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(1L, 0, 5);

        RecommendationDto recommendationDto1 = result.getContent().get(0);
        assertEquals(recommendationDto, recommendationDto1);
    }
}
