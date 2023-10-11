package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import com.kite.kolesnikov.userservice.repository.recommendation.SkillOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillOfferService {

    private final SkillOfferRepository skillOfferRepository;

    @Transactional
    public void save(SkillOffer skillOffer) {
        skillOfferRepository.save(skillOffer);
    }
}
