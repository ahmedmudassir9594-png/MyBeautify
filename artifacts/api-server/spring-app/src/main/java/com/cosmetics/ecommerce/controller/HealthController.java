package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/healthz")
    public ApiResponse<Map<String, String>> health() { return ApiResponse.ok("Service is healthy.", Map.of("status", "ok")); }
}