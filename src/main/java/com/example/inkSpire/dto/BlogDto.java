package com.example.inkSpire.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogDto {
    private String title;
    private String body;
    private String category;
    private MultipartFile image;
}
