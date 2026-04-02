package com.maville.controller;

import com.maville.service.MontrealApiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MontrealDataController {

    private final MontrealApiService montrealApiService;

    public MontrealDataController(MontrealApiService montrealApiService) {
        this.montrealApiService = montrealApiService;
    }

    @GetMapping("/travaux")
    public Map<String, Object> getTravaux(@RequestParam(required = false) String quartier,
                                          @RequestParam(defaultValue = "50") int limit) {
        return montrealApiService.fetchTravaux(quartier, limit);
    }

    @GetMapping("/entraves")
    public Map<String, Object> getEntraves(@RequestParam(required = false) String rue,
                                           @RequestParam(defaultValue = "50") int limit) {
        return montrealApiService.fetchEntraves(rue, limit);
    }
}
