package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.ApiResponse;
import com.cosmetics.ecommerce.dto.ReferralDtos.*;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.service.ReferralService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/referrals")
public class ReferralController {
    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @GetMapping("/me")
    public ApiResponse<ReferralSummaryResponse> getMyReferralSummary(
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            origin = request.getHeader("Referer");
            if (origin != null && origin.contains("/?")) {
                origin = origin.substring(0, origin.indexOf("/?"));
            } else if (origin != null && origin.contains("?")) {
                origin = origin.substring(0, origin.indexOf("?"));
            }
        }
        return ApiResponse.ok("Referral details fetched successfully.", referralService.getSummary(user, origin));
    }

    @GetMapping("/validate")
    public ApiResponse<ValidateReferralResponse> validateReferral(
            @RequestParam String code,
            @AuthenticationPrincipal User user) {
        return ApiResponse.ok("Referral code validated.", referralService.validateReferralCode(code, user));
    }
}
