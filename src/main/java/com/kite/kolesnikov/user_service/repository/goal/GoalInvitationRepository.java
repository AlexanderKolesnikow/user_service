package com.kite.kolesnikov.user_service.repository.goal;

import com.kite.kolesnikov.user_service.entity.goal.GoalInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
}
