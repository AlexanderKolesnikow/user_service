package com.kite.kolesnikov.userservice.repository.contact;

import com.kite.kolesnikov.userservice.entity.contact.ContactPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPreferenceRepository extends JpaRepository<ContactPreference, Long> {
}
