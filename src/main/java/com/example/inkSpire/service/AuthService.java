package com.example.inkSpire.service;

import com.example.inkSpire.dto.LoginDto;
import com.example.inkSpire.dto.LoginResponseDto;
import com.example.inkSpire.dto.SignupDto;
import com.example.inkSpire.dto.UserDetailsDto;
import com.example.inkSpire.entity.AppUser;
import com.example.inkSpire.repository.AppUserRepository;
import com.example.inkSpire.security.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, AuthUtil authUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authUtil = authUtil;
        this.authenticationManager = authenticationManager;
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

    public Object userLogin(LoginDto loginDto) {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );

        AppUser appUser=(AppUser) authentication.getPrincipal();

        String token= authUtil.generateAccessToken(appUser);

        return new LoginResponseDto(token,appUser.getId());
    }
}
