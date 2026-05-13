package com.project.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OtpVerification {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String mobile;
	    private String email;
	    private String otp;

	    @Enumerated(EnumType.STRING)
	    private OtpPurpose purpose;

	    @Column(name = "expires_at")
	    private LocalDateTime expiresAt;
	    
	    private boolean verified;
	    private int attempts;

}
