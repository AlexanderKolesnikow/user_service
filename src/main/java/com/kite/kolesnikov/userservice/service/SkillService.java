package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.dto.skill.SkillDto;
import com.kite.kolesnikov.userservice.dto.skill.SkillOfferDto;
import com.kite.kolesnikov.userservice.dto.skill.UserSkillGuaranteeDto;
import com.kite.kolesnikov.userservice.entity.Skill;
import com.kite.kolesnikov.userservice.entity.UserSkill;
import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.entity.user.UserSkillGuarantee;
import com.kite.kolesnikov.userservice.exception.ResourceNotFoundException;
import com.kite.kolesnikov.userservice.mapper.SkillMapper;
import com.kite.kolesnikov.userservice.mapper.SkillOfferMapper;
import com.kite.kolesnikov.userservice.mapper.UserSkillGuaranteeMapper;
import com.kite.kolesnikov.userservice.repository.SkillRepository;
import com.kite.kolesnikov.userservice.repository.UserSkillGuaranteeRepository;
import com.kite.kolesnikov.userservice.repository.UserSkillRepository;
import com.kite.kolesnikov.userservice.repository.recommendation.SkillOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOfferMapper skillOfferMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final UserSkillRepository userSkillRepository;
    private final UserService userService;

    @Transactional
    public void saveSkillOffer(SkillOfferDto skillOfferDto) {
        SkillOffer skillOffer = skillOfferMapper.toEntity(skillOfferDto);

        long id = skillOfferRepository.save(skillOffer).getId();
        log.info("Skill Guarantee: {} is created", id);
    }

    @Transactional
    public void saveSkillGuarantee(UserSkillGuaranteeDto userSkillGuaranteeDto) {
        UserSkillGuarantee userSkillGuarantee = userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto);

        long id = userSkillGuaranteeRepository.save(userSkillGuarantee).getId();
        log.info("Skill Guarantee: {} is created", id);
    }

    @Transactional
    public void addSkillToUser(long userId, long skillId) {
        User user = userService.getById(userId);
        Skill skill = getSkillById(skillId);

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);

        userSkillRepository.save(userSkill);
        log.info("User: {} added Skill: {} to the profile", userId, skillId);
    }

    @Transactional
    public void deleteSkillFromUser(long userId, long skillId) {
        userSkillRepository.deleteByUserIdAndSkillId(userId, skillId);
        if (!skillRepository.existsById(skillId)) {
            throw new ResourceNotFoundException("Skill not found");
        }

        log.info("User: {} have deleted the Skill: {} from the profile", userId, skillId);
    }

    @Transactional(readOnly = true)
    public Page<SkillDto> getAllUserSkills(long userId,
                                           int pageNumber,
                                           int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Skill> userSkills = skillRepository.findAllByUsersId(userId, pageable);

        return userSkills.map(skillMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format("Skill under ID: {0} not found)", skillId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    @Transactional(readOnly = true)
    public boolean userHaveSkill(long skillId, long userId) {
        return skillRepository.existsByIdAndUsers_Id(skillId, userId);
    }

    @Transactional(readOnly = true)
    public boolean guaranteeExist(long userId, long skillId, long guarantorId) {
        return userSkillGuaranteeRepository.existsByUserIdAndSkillIdAndGuarantorId(userId, skillId, guarantorId);
    }

    @Transactional(readOnly = true)
    public boolean skillsExistById(List<Long> skillIds) {
        int count = skillRepository.countExisting(skillIds);
        return (count == skillIds.size());
    }
}
