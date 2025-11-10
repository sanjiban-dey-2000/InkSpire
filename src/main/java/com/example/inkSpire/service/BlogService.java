package com.example.inkSpire.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.inkSpire.dto.BlogDto;
import com.example.inkSpire.dto.BlogResponseDto;
import com.example.inkSpire.dto.GetBlogDto;
import com.example.inkSpire.entity.AppUser;
import com.example.inkSpire.entity.Blog;
import com.example.inkSpire.repository.AppUserRepository;
import com.example.inkSpire.repository.BlogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;
    private final AppUserRepository userRepository;
    private final Cloudinary cloudinary;

    public BlogService(BlogRepository blogRepository,
                       ModelMapper modelMapper,
                       AppUserRepository userRepository,
                       Cloudinary cloudinary) {
        this.blogRepository = blogRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
    }

    public Object addBlogs(BlogDto blogDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) auth.getPrincipal();

        AppUser existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User doesn't exist"));

        Blog blog = Blog.builder()
                .title(blogDto.getTitle())
                .body(blogDto.getBody())
                .createdAt(LocalDateTime.now())
                .user(existingUser)
                .build();

        try {
            if (blogDto.getImageUrl() != null && !blogDto.getImageUrl().isEmpty()) {
                // Cloudinary upload handled in controller
                blog.setImagePath(blogDto.getImageUrl());
            }
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }

        existingUser.getBlogs().add(blog);
        blog = blogRepository.save(blog);
        return modelMapper.map(blog, BlogResponseDto.class);
    }

    public List<GetBlogDto> getAllBlogs() {
        return blogRepository.findAll().stream().map(blog -> {
            GetBlogDto dto = modelMapper.map(blog, GetBlogDto.class);
            dto.setImageUrl(blog.getImagePath());
            return dto;
        }).toList();
    }

    public List<BlogResponseDto> getMyBlogs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) auth.getPrincipal();

        AppUser existingUser = userRepository.findById(user.getId()).orElseThrow();

        return existingUser.getBlogs().stream().map(blog -> {
            BlogResponseDto dto = modelMapper.map(blog, BlogResponseDto.class);
            dto.setImageUrl(blog.getImagePath());
            return dto;
        }).toList();
    }

    public void deleteByBlog(Long blogId) {
        if (!blogRepository.existsById(blogId)) {
            throw new ResourceAccessException("Blog not found with id " + blogId);
        }
        blogRepository.deleteById(blogId);
    }

    public Object updateBlogById(Long blogId, BlogDto blogDto) {
        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id " + blogId));

        existingBlog.setTitle(blogDto.getTitle());
        existingBlog.setBody(blogDto.getBody());
        if (blogDto.getImageUrl() != null) {
            existingBlog.setImagePath(blogDto.getImageUrl());
        }

        existingBlog = blogRepository.save(existingBlog);
        return modelMapper.map(existingBlog, BlogResponseDto.class);
    }

    public List<GetBlogDto> searchByKeyword(String keyword) {
        List<Blog> blogs = blogRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(keyword, keyword);
        return blogs.stream().map(blog -> {
            GetBlogDto dto = modelMapper.map(blog, GetBlogDto.class);
            dto.setImageUrl(blog.getImagePath());
            return dto;
        }).toList();
    }

    public Object getBlogById(Long BlogId) {
        Blog blogDetails = blogRepository.findById(BlogId)
                .orElseThrow(() -> new IllegalArgumentException("No blog found"));
        GetBlogDto dto = modelMapper.map(blogDetails, GetBlogDto.class);
        dto.setImageUrl(blogDetails.getImagePath());
        return dto;
    }
}
