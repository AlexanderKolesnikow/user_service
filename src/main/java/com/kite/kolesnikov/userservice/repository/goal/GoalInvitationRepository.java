package com.kite.kolesnikov.userservice.repository.goal;

import com.kite.kolesnikov.userservice.entity.goal.GoalInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
}
