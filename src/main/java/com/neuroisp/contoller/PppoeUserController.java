package com.neuroisp.contoller;

import com.neuroisp.entity.PppoeUser;
import com.neuroisp.service.PppoeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pppoe/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PppoeUserController {

    private final PppoeUserService userService;

    @PostMapping("/{routerId}")
    public PppoeUser create(
            @PathVariable String routerId,
            @RequestBody PppoeUser user
    ) {
        return userService.createUser(user, routerId);
    }
    @GetMapping
    public List<PppoeUser> getAllUsers() {
        return userService.getAllUsers();
    }
}
