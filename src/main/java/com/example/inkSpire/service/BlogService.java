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
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public Object addBlogs(BlogDto blogDto) throws IOException {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        AppUser user=(AppUser) auth.getPrincipal();
        AppUser existingUser=userRepository.findById(user.getId()).orElseThrow(()->new IllegalArgumentException("User doesn't exist"));
        Blog blog=Blog.builder()
                        .title(blogDto.getTitle())
                        .body(blogDto.getBody())
                        .createdAt(LocalDateTime.now())
                        .user(existingUser)
                        .build();
        //handle image upload
        if(blogDto.getImage()!=null && !blogDto.getImage().isEmpty()){
            String uploadDir="uploads/";
            String originalFilename=blogDto.getImage().getOriginalFilename();
            String fileName= UUID.randomUUID()+"_"+originalFilename;

            Path uploadPath= Paths.get(uploadDir);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream=blogDto.getImage().getInputStream()){
                Path filePath=uploadPath.resolve(fileName);
                Files.copy(inputStream,filePath, StandardCopyOption.REPLACE_EXISTING);
                blog.setImagePath(fileName);
            }catch(IOException ex){
                throw new IOException("Could not save image file "+originalFilename,ex);
            }
        }

        existingUser.getBlogs().add(blog);
        blog=blogRepository.save(blog);

        return modelMapper.map(blog, BlogResponseDto.class);
    }

    public List<GetBlogDto> getAllBlogs() {
        List<Blog> blogs= blogRepository.findAll();
        return blogs.stream().map(blog->{
            GetBlogDto dto=modelMapper.map(blog,GetBlogDto.class);
            //if image exists, build full URL
            if(blog.getImagePath()!=null){
                dto.setImageUrl("http://localhost:8080/uploads/"+blog.getImagePath());
            }
            return dto;
        }).toList();
    }


    public List<BlogResponseDto> getMyBlogs() {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        AppUser user=(AppUser) auth.getPrincipal();
        AppUser existingUser=userRepository.findById(user.getId()).orElseThrow();

        return existingUser.getBlogs().stream().map(blog->{
            BlogResponseDto blogResDto=modelMapper.map(blog,BlogResponseDto.class);

            if(blog.getImagePath()!=null){
                blogResDto.setImageUrl("http://localhost:8080/uploads/"+blog.getImagePath());
            }
            return blogResDto;
        }).toList();

    }

    public void deleteByBlog(Long blogId){
        if(!blogRepository.existsById(blogId)){
            throw new ResourceAccessException("Blog not found with id "+blogId);
        }

        blogRepository.deleteById(blogId);

    }

    public Object updateBlogById(Long blogId, BlogDto blogDto) {
        Blog existingBlog=blogRepository.findById(blogId).orElseThrow(()->new IllegalArgumentException("Blog not found with id "+blogId));
        Blog updateDetails=modelMapper.map(blogDto,Blog.class);
        existingBlog.setTitle(updateDetails.getTitle());
        existingBlog.setBody(updateDetails.getBody());
        existingBlog=blogRepository.save(existingBlog);
        return modelMapper.map(existingBlog,BlogResponseDto.class);
    }

    public List<GetBlogDto> searchByKeyword(String keyword) {
        List<Blog> blogs=blogRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(keyword,keyword);
        return blogs.stream().map(blog->{
            GetBlogDto dto=modelMapper.map(blog,GetBlogDto.class);
            //if image exists, build full URL
            if(blog.getImagePath()!=null){
                dto.setImageUrl("http://localhost:8080/uploads/"+blog.getImagePath());
            }
            return dto;
        }).toList();
    }

    public Object getBlogById(Long BlogId) {
        Blog blogDetails=blogRepository.findById(BlogId).orElseThrow(()->new IllegalArgumentException("No blog found"));
        GetBlogDto dto=modelMapper.map(blogDetails,GetBlogDto.class);
        if(blogDetails.getImagePath()!=null){
            dto.setImageUrl("http://localhost:8080/uploads/" + blogDetails.getImagePath());
        }

        return dto;
    }
}
