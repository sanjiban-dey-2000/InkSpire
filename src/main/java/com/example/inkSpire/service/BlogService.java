package com.example.inkSpire.service;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;
    private final AppUserRepository userRepository;


    public BlogService(BlogRepository blogRepository, ModelMapper modelMapper, AppUserRepository userRepository, AppUserRepository userRepository1) {
        this.blogRepository = blogRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository1;
    }

    public Object addBlogs(BlogDto blogDto) {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        AppUser user=(AppUser) auth.getPrincipal();
        AppUser existingUser=userRepository.findById(user.getId()).orElseThrow(()->new IllegalArgumentException("User doesn't exist"));
        Blog blog=Blog.builder()
                        .title(blogDto.getTitle())
                        .body(blogDto.getBody())
                        .createdAt(LocalDateTime.now())
                        .user(existingUser)
                        .build();
        existingUser.getBlogs().add(blog);
        blog=blogRepository.save(blog);

        return modelMapper.map(blog, BlogResponseDto.class);
    }

    public List<GetBlogDto> getAllBlogs() {
        List<Blog> blog= blogRepository.findAll();
        List<GetBlogDto> blogDetailsDto=blog.stream().map(blogs->modelMapper.map(blogs,GetBlogDto.class)).toList();

        return blogDetailsDto;
    }


    public List<BlogDto> getMyBlogs() {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        AppUser user=(AppUser) auth.getPrincipal();
        AppUser existingUser=userRepository.findById(user.getId()).orElseThrow();

        return existingUser.getBlogs().stream().map(blog->modelMapper.map(blog,BlogDto.class)).toList();
    }
}
