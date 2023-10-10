package com.kite.kolesnikov.user_service.repository;

import com.kite.kolesnikov.user_service.entity.ProjectSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSubscriptionRepository extends JpaRepository<ProjectSubscription, Long> {
}
