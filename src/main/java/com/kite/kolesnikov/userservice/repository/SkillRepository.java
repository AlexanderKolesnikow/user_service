package com.kite.kolesnikov.userservice.repository;

import com.kite.kolesnikov.userservice.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByTitleIgnoreCase(String title);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM skill WHERE id IN (?1)")
    int countExisting(Iterable<Long> ids);

    Page<Skill> findAllByUsersId(long userId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN skill_offer so ON so.skill_id = s.id
            JOIN recommendation r ON r.id = so.recommendation_id
            WHERE r.receiver_id = :userId
            """)
    List<Skill> findSkillsOfferedToUser(long userId);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN user_skill us ON us.skill_id = :skillId AND us.user_id = :userId
            """)
    Optional<Skill> findUserSkill(long skillId, long userId);

    @Query(nativeQuery = true, value = "INSERT INTO user_skill (skill_id, user_id) VALUES (:skillId, :userId)")
    @Modifying
    void assignSkillToUser(long skillId, long userId);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            WHERE s.id IN (SELECT gs.skill_id FROM goal_skill gs
            WHERE gs.goal_id = ?1)
            """)
    List<Skill> findSkillsByGoalId(long goalId);

    boolean existsByIdAndUsers_Id(long skillId, long userId);
}
