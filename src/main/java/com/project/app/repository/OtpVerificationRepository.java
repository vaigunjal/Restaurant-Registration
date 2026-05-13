package com.project.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.model.OtpPurpose;
import com.project.app.model.OtpVerification;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> 
{
	Optional<OtpVerification> findTopByEmailAndPurposeOrderByIdDesc(String email, OtpPurpose purpose);

	Optional<OtpVerification> findTopByMobileAndPurposeOrderByIdDesc(String mobile, OtpPurpose purpose);
}
