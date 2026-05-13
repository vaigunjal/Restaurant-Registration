package com.project.app.service;


import java.time.LocalDateTime;



import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.app.dto.ForgotPasswordOtpRequest;
import com.project.app.dto.ResetPasswordRequest;
import com.project.app.dto.VerifyOtpRequest;
import com.project.app.model.OtpPurpose;
import com.project.app.model.OtpVerification;
import com.project.app.model.User;
import com.project.app.repository.OtpVerificationRepository;
import com.project.app.repository.UserRepository;


@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // SEND OTP
    // =========================
    public String sendOtp(ForgotPasswordOtpRequest request) {

        String otp = String.valueOf(
                1000 + new Random().nextInt(9000)
        );

        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setOtp(otp);
        otpEntity.setPurpose(OtpPurpose.FORGOT_PASSWORD);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);
        otpEntity.setAttempts(0);

        // EMAIL FLOW
        if (request.getEmail() != null) {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Account does not exist"));

            otpEntity.setEmail(request.getEmail());

            emailService.sendOtpEmail(request.getEmail(), otp);
        }

        // MOBILE FLOW
        else if (request.getMobile() != null) {

            User user = userRepository.findByMobile(request.getMobile())
                    .orElseThrow(() -> new RuntimeException("Account does not exist"));

            otpEntity.setMobile(request.getMobile());

            smsService.sendOtpSms(request.getMobile(), otp);
        }

        else {
            throw new RuntimeException("Email or Mobile required");
        }

        otpRepository.save(otpEntity);

        return "OTP sent successfully";
    }

    // =========================
    // VERIFY OTP
    // =========================
    public String verifyOtp(VerifyOtpRequest request) {

        OtpVerification otpEntity;

        if (request.getEmail() != null) {

            otpEntity = otpRepository
                    .findTopByEmailAndPurposeOrderByIdDesc(
                            request.getEmail(),
                            OtpPurpose.FORGOT_PASSWORD
                    )
                    .orElseThrow(() -> new RuntimeException("OTP not found"));
        } else {

            otpEntity = otpRepository
                    .findTopByMobileAndPurposeOrderByIdDesc(
                            request.getMobile(),
                            OtpPurpose.FORGOT_PASSWORD
                    )
                    .orElseThrow(() -> new RuntimeException("OTP not found"));
        }

        // ❌ OTP EXPIRY CHECK
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // ❌ MAX ATTEMPTS CHECK
        if (otpEntity.getAttempts() >= 5) {
            throw new RuntimeException("Too many attempts");
        }

        // ❌ WRONG OTP
        if (!otpEntity.getOtp().equals(request.getOtp())) {

            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            otpRepository.save(otpEntity);

            throw new RuntimeException("Incorrect OTP");
        }

        // ✅ SUCCESS
        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return "OTP verified successfully";
    }

    // =========================
    // RESET PASSWORD
    // =========================
    public String resetPassword(ResetPasswordRequest request) {

        User user;
        OtpVerification otpEntity;

        // EMAIL FLOW
        if (request.getEmail() != null) {

            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Account does not exist"));

            otpEntity = otpRepository
                    .findTopByEmailAndPurposeOrderByIdDesc(
                            request.getEmail(),
                            OtpPurpose.FORGOT_PASSWORD
                    )
                    .orElseThrow(() -> new RuntimeException("OTP not found"));

        } else {

            user = userRepository.findByMobile(request.getMobile())
                    .orElseThrow(() -> new RuntimeException("Account does not exist"));

            otpEntity = otpRepository
                    .findTopByMobileAndPurposeOrderByIdDesc(
                            request.getMobile(),
                            OtpPurpose.FORGOT_PASSWORD
                    )
                    .orElseThrow(() -> new RuntimeException("OTP not found"));
        }

        // ❌ MUST VERIFY OTP FIRST
        if (!otpEntity.isVerified()) {
            throw new RuntimeException("Verify OTP first");
        }

        // ❌ PASSWORD VALIDATION
        if (request.getNewPassword() == null
                || request.getNewPassword().length() < 8) {
            throw new RuntimeException("Use strong password");
        }

        // 🔐 HASH PASSWORD
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // 🔒 INVALIDATE OTP
        otpEntity.setOtp(null);
        otpEntity.setVerified(false);
        otpRepository.save(otpEntity);

        return "Password reset successfully";
    }
}
