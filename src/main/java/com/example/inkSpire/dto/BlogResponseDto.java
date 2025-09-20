package com.example.inkSpire.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogResponseDto {
    private Long id;
    private String title;
    private String body;
}
