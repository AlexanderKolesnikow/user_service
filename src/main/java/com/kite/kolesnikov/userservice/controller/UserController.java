package com.kite.kolesnikov.userservice.controller;

import com.kite.kolesnikov.userservice.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {
    private final UserService userService;

    @GetMapping
    public long getUserIdByContact(@RequestParam String contact) {
        return userService.getUserIdByContact(contact);
    }
}
