package com.example.inkSpire.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogDto {
    private String title;
    private String body;
}
