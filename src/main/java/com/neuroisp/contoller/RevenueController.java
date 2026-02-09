package com.neuroisp.contoller;

import com.neuroisp.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports/revenue")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping("/today")
    public double today() {
        return revenueService.todayRevenue();
    }

    @GetMapping("/month")
    public double month() {
        return revenueService.monthlyRevenue();
    }

    @GetMapping("/range")
    public double range(
            @RequestParam String start,
            @RequestParam String end
    ) {
        return revenueService.rangeRevenue(
                LocalDate.parse(start),
                LocalDate.parse(end)
        );
    }

    @GetMapping("/daily")
    public List<Object[]> daily() {
        return revenueService.dailyBreakdown();
    }
}
