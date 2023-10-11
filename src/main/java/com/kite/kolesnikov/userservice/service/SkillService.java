package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.entity.Skill;
import com.kite.kolesnikov.userservice.exception.DataValidationException;
import com.kite.kolesnikov.userservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional
    public void save(Skill skill) {
        skillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public Skill getById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));
    }

    public boolean existsById(long skillId) {
        return skillRepository.existsById(skillId);
    }
}
