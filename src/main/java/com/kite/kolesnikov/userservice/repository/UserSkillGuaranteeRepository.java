package com.kite.kolesnikov.userservice.repository;

import com.kite.kolesnikov.userservice.entity.user.UserSkillGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
    boolean existsByUserIdAndSkillIdAndGuarantorId(long userId, long skillId, long guarantorId);
}
