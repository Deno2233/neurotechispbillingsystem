package com.neuroisp.contoller;

import com.neuroisp.dto.HotspotActiveClient;
import com.neuroisp.dto.PppoeActiveClient;
import com.neuroisp.entity.MikrotikRouter;
import com.neuroisp.repository.MikrotikRouterRepository;
import com.neuroisp.service.MikrotikActiveClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/mikrotik/active")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")




public class MikrotikActiveClientController {

    private final MikrotikActiveClientService activeClientService;

    @GetMapping("/hotspot")
    public List<HotspotActiveClient> hotspotActiveUsers() {
        return activeClientService.getActiveHotspotUsers();
    }

    @GetMapping("/pppoe")
    public List<PppoeActiveClient> pppoeActiveUsers() {
        return activeClientService.getActivePppoeUsers();
    }
    @GetMapping("/hotspot/count")
    public int activeHotspotCount() {
        return activeClientService.getActiveHotspotUserCount();
    }

    @GetMapping("/pppoe/count")
    public int activePppoeCount() {
        return activeClientService.getActivePppoeUserCount();
    }

    @GetMapping("/total")
    public int totalActiveUsers() {
        return activeClientService.getTotalActiveUsers();
    }
}
