package com.example.inkSpire.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    private String username;
    private String password;
    private String email;
}
