package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.entity.UserAction;
import com.wedding.dreamwedding.repository.UserActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/actions")
public class UserActionController {

    @Autowired
    private UserActionRepository userActionRepository;

    @PostMapping("/log")
    public void logAction(@RequestBody UserAction action) {
        action.setTimestamp(LocalDateTime.now());
        userActionRepository.save(action);
    }
}
