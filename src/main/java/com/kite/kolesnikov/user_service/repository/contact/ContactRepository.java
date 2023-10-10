package com.kite.kolesnikov.user_service.repository.contact;

import com.kite.kolesnikov.user_service.entity.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
