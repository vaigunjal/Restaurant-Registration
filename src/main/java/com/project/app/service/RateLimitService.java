package com.project.app.service;


import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;


@Service
public class RateLimitService {

    // store count for OTP requests
    private Map<String, Integer> otpRequestCount = new HashMap<>();
    private Map<String, LocalDateTime> otpRequestTime = new HashMap<>();

    // store verify attempts
    private Map<String, Integer> otpVerifyAttempts = new HashMap<>();

    // store reset password requests
    private Map<String, Integer> resetPasswordCount = new HashMap<>();
    private Map<String, LocalDateTime> resetPasswordTime = new HashMap<>();

    // =========================
    // 1. FORGOT PASSWORD OTP (3 / 30 min)
    // =========================
    public void checkOtpRequestLimit(String key) {

        LocalDateTime now = LocalDateTime.now();

        if (otpRequestTime.containsKey(key)) {

            LocalDateTime lastTime = otpRequestTime.get(key);

            // reset after 30 minutes
            if (lastTime.plusMinutes(30).isBefore(now)) {
                otpRequestCount.put(key, 0);
            }
        }

        int count = otpRequestCount.getOrDefault(key, 0);

        if (count >= 3) {
            throw new RuntimeException("Too many OTP requests. Try after 30 minutes");
        }

        otpRequestCount.put(key, count + 1);
        otpRequestTime.put(key, now);
    }

    // =========================
    // 2. VERIFY OTP (5 attempts)
    // =========================
    public void checkOtpVerifyLimit(String key) {

        int attempts = otpVerifyAttempts.getOrDefault(key, 0);

        if (attempts >= 5) {
            throw new RuntimeException("Too many OTP attempts. Try again later");
        }

        otpVerifyAttempts.put(key, attempts + 1);
    }

    public void resetOtpAttempts(String key) {
        otpVerifyAttempts.remove(key);
    }

    // =========================
    // 3. RESET PASSWORD (5 / 30 min)
    // =========================
    public void checkResetPasswordLimit(String key) {

        LocalDateTime now = LocalDateTime.now();

        if (resetPasswordTime.containsKey(key)) {

            LocalDateTime lastTime = resetPasswordTime.get(key);

            if (lastTime.plusMinutes(30).isBefore(now)) {
                resetPasswordCount.put(key, 0);
            }
        }

        int count = resetPasswordCount.getOrDefault(key, 0);

        if (count >= 5) {
            throw new RuntimeException("Too many reset requests. Try again later");
        }

        resetPasswordCount.put(key, count + 1);
        resetPasswordTime.put(key, now);
    }
}