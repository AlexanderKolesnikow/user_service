package com.kite.kolesnikov.userservice.repository.mentorship;

import com.kite.kolesnikov.userservice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {
}
