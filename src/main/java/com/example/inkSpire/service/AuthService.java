package com.example.inkSpire.service;

import com.example.inkSpire.dto.SignupDto;
import com.example.inkSpire.dto.UserDetailsDto;
import com.example.inkSpire.entity.AppUser;
import com.example.inkSpire.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Object userSignup(UserDetailsDto userDetailsDto) {
        AppUser appUser=userRepository.findByEmail(userDetailsDto.getEmail()).orElse(null);

        if(appUser!=null){
            throw new IllegalArgumentException("User is already there with email id "+userDetailsDto.getEmail());
        }

        appUser=userRepository.save(AppUser.builder()
                .username(userDetailsDto.getUsername())
                .password(passwordEncoder.encode(userDetailsDto.getPassword()))
                .email(userDetailsDto.getEmail())
                .build()
        );

        return new SignupDto(appUser.getId(),appUser.getUsername());
    }
}
