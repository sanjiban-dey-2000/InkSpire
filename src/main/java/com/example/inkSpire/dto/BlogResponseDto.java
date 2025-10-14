package com.example.inkSpire.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogResponseDto {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private String imageUrl;
}
