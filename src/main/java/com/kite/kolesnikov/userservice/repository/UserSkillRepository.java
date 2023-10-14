package com.kite.kolesnikov.userservice.repository;

import com.kite.kolesnikov.userservice.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    void deleteByUserIdAndSkillId(long userId, long skillId);
}
