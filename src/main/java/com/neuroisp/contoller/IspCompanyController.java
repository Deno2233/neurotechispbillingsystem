package com.neuroisp.contoller;

import com.neuroisp.entity.IspCompany;
import com.neuroisp.service.IspCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/isps")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class IspCompanyController {

    private final IspCompanyService ispService;

    @PostMapping
    public IspCompany create(@RequestBody IspCompany isp) {
        return ispService.create(isp);
    }

    @PutMapping("/{id}")
    public IspCompany update(
            @PathVariable String id,
            @RequestBody IspCompany isp
    ) {
        return ispService.update(id, isp);
    }

    @GetMapping
    public List<IspCompany> getAll() {
        return ispService.findAll();
    }

    @GetMapping("/{id}")
    public IspCompany getOne(@PathVariable String id) {
        return ispService.findById(id);
    }

    @PostMapping("/{ispId}/sms-provider/{providerId}")
    public void assignSmsProvider(
            @PathVariable String ispId,
            @PathVariable String providerId
    ) {
        ispService.assignSmsProvider(ispId, providerId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        ispService.delete(id);
    }
    @GetMapping("/active")
    public List<IspCompany> getActiveCompanies() {
        return ispService.findActive();
    }

}
