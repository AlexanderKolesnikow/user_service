package com.kite.kolesnikov.userservice.repository;

import com.kite.kolesnikov.userservice.entity.ProjectSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSubscriptionRepository extends JpaRepository<ProjectSubscription, Long> {
}
