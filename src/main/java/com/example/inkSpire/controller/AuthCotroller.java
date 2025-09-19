package com.example.inkSpire.controller;

import com.example.inkSpire.dto.LoginDto;
import com.example.inkSpire.dto.UserDetailsDto;
import com.example.inkSpire.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthCotroller {

    private final AuthService authService;

    public AuthCotroller(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> userSignup(@RequestBody UserDetailsDto userDetailsDto){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.userSignup(userDetailsDto));
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginDto loginDto){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(authService.userLogin(loginDto));
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.FORBIDDEN);
        }
    }
}
