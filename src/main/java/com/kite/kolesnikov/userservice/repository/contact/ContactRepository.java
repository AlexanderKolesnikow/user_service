package com.kite.kolesnikov.userservice.repository.contact;

import com.kite.kolesnikov.userservice.entity.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Long> findUserIdByContact(String contact);
}
