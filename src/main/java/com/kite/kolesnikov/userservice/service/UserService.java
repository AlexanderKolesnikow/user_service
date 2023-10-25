package com.kite.kolesnikov.userservice.service;

import com.kite.kolesnikov.userservice.entity.user.User;
import com.kite.kolesnikov.userservice.exception.ResourceNotFoundException;
import com.kite.kolesnikov.userservice.repository.UserRepository;
import com.kite.kolesnikov.userservice.repository.contact.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format("The user with ID: {} does not exist", userId);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    @Transactional
    public Long getUserIdByContact(String contact) {
        return contactRepository.findUserIdByContact(contact)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format("The user with {} this contact does not exist", contact);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }
}
