package com.kite.kolesnikov.userservice.controller;

import com.kite.kolesnikov.userservice.dto.skill.SkillDto;
import com.kite.kolesnikov.userservice.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
@Tag(name = "Skill controller")
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new skill to User profile")
    public void addSkillToUserProfile(@RequestParam long userId,
                                      @RequestParam long skillId) {

        skillService.addSkillToUser(userId, skillId);
    }

    @DeleteMapping("/{skillId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a skill from User profile")
    public void deleteSkillFromUserProfile(@RequestParam long userId,
                                           @PathVariable long skillId) {

        skillService.deleteSkillFromUser(userId, skillId);
    }

    @GetMapping
    @Operation(summary = "Get all User skills")
    public Page<SkillDto> getAllUserSkills(@RequestParam long userId,
                                           @RequestParam(defaultValue = "0") int pageNumber,
                                           @RequestParam(defaultValue = "10") int pageSize) {

        return skillService.getAllUserSkills(userId, pageNumber, pageSize);
    }
}
