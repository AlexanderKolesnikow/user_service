package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.entity.Skill;
import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import com.kite.kolesnikov.userservice.repository.SkillRepository;
import com.kite.kolesnikov.userservice.repository.recommendation.SkillOfferRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;

    @Transactional
    public void saveSkill(Skill skill) {
        skillRepository.save(skill);
    }

    @Transactional
    public void saveSkillOffer(SkillOffer skillOffer) {
        skillOfferRepository.save(skillOffer);
    }

    @Transactional(readOnly = true)
    public Skill getById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format("Skill under ID: {0} not found)", skillId);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }

    public void skillExistById(long skillId) {
        if (!skillRepository.existsById(skillId)) {
            String errorMessage = MessageFormat.format("Skill with ID: {0} does not exist", skillId);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
    }
}
