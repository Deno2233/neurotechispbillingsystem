package com.neuroisp.contoller;

import com.neuroisp.service.BytewaveProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bytewave")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class BytewaveProfileController {

    private final BytewaveProfileService bytewaveProfileService;

    @GetMapping("/profile")
    public String getProfile() throws Exception {
        return bytewaveProfileService.getProfile();
    }
}
