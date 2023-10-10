package com.kite.kolesnikov.user_service.repository.recommendation;

import com.kite.kolesnikov.user_service.entity.recommendation.SkillRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRequestRepository extends JpaRepository<SkillRequest, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO skill_request (request_id, skill_id)
            VALUES (:requestId, :skillId)
            """)
    @Modifying
    SkillRequest create(long requestId, long skillId);
}
