package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.skill.SkillDto;
import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.entity.Skill;
import com.kite.kolesnikov.userservice.entity.UserSkill;
import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.exception.ResourceNotFoundException;
import com.kite.kolesnikov.userservice.mapper.SkillMapperImpl;
import com.kite.kolesnikov.userservice.mapper.SkillOfferMapperImpl;
import com.kite.kolesnikov.userservice.mapper.UserSkillGuaranteeMapperImpl;
import com.kite.kolesnikov.userservice.repository.SkillRepository;
import com.kite.kolesnikov.userservice.repository.UserSkillGuaranteeRepository;
import com.kite.kolesnikov.userservice.repository.UserSkillRepository;
import com.kite.kolesnikov.userservice.repository.recommendation.SkillOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Spy
    private SkillMapperImpl skillMapper;
    @Spy
    private UserSkillGuaranteeMapperImpl userSkillGuaranteeMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserSkillRepository userSkillRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private SkillService skillService;

    @Test
    void testCreateSkill_Successful() {
        SkillDto skillDto = SkillDto.builder().title("Java").build();

        when(skillRepository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenReturn(Skill.builder().id(1L).build());

        skillService.createSkill(skillDto);

        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void testCreateSkill_SkillAlreadyExist() {
        SkillDto skillDto = SkillDto.builder().title("Java").build();

        when(skillRepository.existsByTitleIgnoreCase(anyString())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.createSkill(skillDto));
    }

    @Test
    public void testSaveSkillOffer() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        SkillOffer skillOffer = new SkillOffer();

        when(skillOfferMapper.toEntity(skillOfferDto)).thenReturn(skillOffer);
        when(skillOfferRepository.save(skillOffer)).thenReturn(skillOffer);

        skillService.saveSkillOffer(skillOfferDto);

        verify(skillOfferRepository).save(skillOffer);
    }

    @Test
    public void testAddSkillToUser() {
        long userId = 1L;
        long skillId = 1L;
        User user = new User();
        Skill skill = new Skill();

        when(userService.getById(userId)).thenReturn(user);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        skillService.addSkillToUser(userId, skillId);

        verify(userService).getById(userId);
        verify(skillRepository).findById(skillId);
        verify(userSkillRepository).save(any(UserSkill.class));
    }

    @Test
    public void testGetAllUserSkills() {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Skill> page = new PageImpl<>(List.of(new Skill()));

        when(skillRepository.findAllByUsersId(userId, pageable)).thenReturn(page);

        skillService.getAllUserSkills(userId, pageNumber, pageSize);

        verify(skillRepository).findAllByUsersId(userId, pageable);
    }

    @Test
    public void testDeleteSkillFromUser() {
        long userId = 1L;
        long skillId = 1L;

        skillService.deleteSkillFromUser(userId, skillId);

        verify(userSkillRepository).deleteByUserIdAndSkillId(userId, skillId);
    }

    @Test
    public void testGetSkillById_NotFound() {
        long skillId = 1L;

        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> skillService.getSkillById(skillId));
    }

    @Test
    public void testGetSkillById_Found() {
        long skillId = 1L;
        Skill skill = new Skill();

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        Skill foundSkill = skillService.getSkillById(skillId);

        assertEquals(skill, foundSkill);
    }
}
