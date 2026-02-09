package com.neuroisp.service;

import com.neuroisp.entity.MikrotikRouter;
import com.neuroisp.entity.PppoeUser;
import com.neuroisp.repository.MikrotikRouterRepository;
import com.neuroisp.repository.PppoeUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PppoeUserService {

    private final PppoeUserRepository userRepo;
    private final MikrotikRouterRepository routerRepo;

    public PppoeUser createUser(PppoeUser user, String routerId) {

        MikrotikRouter router = routerRepo.findById(routerId)
                .orElseThrow(() -> new RuntimeException("Router not found"));

        user.setRouter(router);
        return userRepo.save(user);
    }
    // ✅ GET ALL USERS
    public List<PppoeUser> getAllUsers() {
        return userRepo.findAll();
    }
}
