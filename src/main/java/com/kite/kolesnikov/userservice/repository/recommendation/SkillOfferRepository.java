package com.kite.kolesnikov.userservice.repository.recommendation;

import com.kite.kolesnikov.userservice.entity.recommendation.SkillOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillOfferRepository extends JpaRepository<SkillOffer, Long> {

    void deleteAllByRecommendationId(long recommendationId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(so.id) FROM skill_offer so
            JOIN recommendation r ON r.id = so.recommendation_id AND r.receiver_id = :userId
            WHERE so.skill_id = :skillId
            """)
    int countAllOffersOfSkill(long skillId, long userId);

    @Query(value = """
            SELECT so FROM SkillOffer so
            JOIN so.recommendation r
            WHERE so.skill.id = :skillId AND r.receiver.id = :userId
            """)
    List<SkillOffer> findAllOffersOfSkill(long skillId, long userId);

    @Query(value = """
            SELECT so FROM SkillOffer so
            JOIN so.recommendation r
            WHERE r.receiver.id = :userId
            """)
    List<SkillOffer> findAllByUserId(long userId);
}
