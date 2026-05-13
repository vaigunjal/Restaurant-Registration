package com.project.app.service;

import org.springframework.stereotype.Service;


@Service
public class SmsService {

    public void sendOtpSms(
            String mobile,
            String otp) {

        // TEMPORARY SMS LOGIC
        System.out.println(
                "Mobile OTP : "
                        + otp
                        + " sent to "
                        + mobile
        );
    }
}