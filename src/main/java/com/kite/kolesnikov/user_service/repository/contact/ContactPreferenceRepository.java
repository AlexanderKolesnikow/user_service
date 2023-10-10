package com.kite.kolesnikov.user_service.repository.contact;

import com.kite.kolesnikov.user_service.entity.contact.ContactPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPreferenceRepository extends JpaRepository<ContactPreference, Long> {
}
