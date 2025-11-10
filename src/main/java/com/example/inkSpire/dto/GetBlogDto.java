package com.example.inkSpire.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBlogDto {

    private Long id;
    private String title;
    private String body;
    private String category;
    private LocalDateTime createdAt;
    private String imageUrl;
    private  UserDto user;
}
