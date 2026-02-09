package com.neuroisp.service;

import com.neuroisp.entity.MikrotikRouter;
import com.neuroisp.repository.MikrotikRouterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveRouterResolverService {

    private final MikrotikRouterRepository routerRepository;

    public MikrotikRouter getActiveRouter() {
        return routerRepository.findByActiveTrue()
                .orElseThrow(() ->
                        new RuntimeException("No active MikroTik router configured"));
    }
}
