package com.example.inkSpire.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.inkSpire.config.CloudinaryConfig;
import com.example.inkSpire.dto.BlogDto;
import com.example.inkSpire.dto.BlogResponseDto;
import com.example.inkSpire.dto.GetBlogDto;
import com.example.inkSpire.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class BlogController {

    private final BlogService blogService;
    private final Cloudinary cloudinary;

    public BlogController(BlogService blogService, Cloudinary cloudinary) {
        this.blogService = blogService;
        this.cloudinary=cloudinary;
    }

    @PostMapping("/blog/add")
    public ResponseEntity<?> addBlogs(@RequestParam("title") String title, @RequestParam("body") String body, @RequestParam(value="image",required=false)MultipartFile image){
        try{
            BlogDto blogDto=new BlogDto();
            blogDto.setTitle(title);
            blogDto.setBody(body);
            if(image!=null && !image.isEmpty()){
                Map uploadResult=cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl=uploadResult.get("secure_url").toString();
                blogDto.setImageUrl(imageUrl);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(blogService.addBlogs(blogDto));
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/blogs/")
    public ResponseEntity<?> getAllBlogs(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogService.getAllBlogs());
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/myblogs")
    public ResponseEntity<?> getMyBlogs(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogService.getMyBlogs());
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/blog/{BlogId}")
    public ResponseEntity<?> getBlogById(@PathVariable Long BlogId){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogService.getBlogById(BlogId));
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/blog/{blogId}/remove")
    public ResponseEntity<?> deleteBlogByID(@PathVariable Long blogId){
        try{
            blogService.deleteByBlog(blogId);
            return ResponseEntity.noContent().build();
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/blog/{blogId}/update")
    public ResponseEntity<?> updateBlogById(@PathVariable Long blogId,@RequestBody BlogDto blogDto){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(blogService.updateBlogById(blogId,blogDto));
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/blogs/search")
    public ResponseEntity<List<GetBlogDto>> searchByKeyword(@RequestParam("q") String keyword){
        return ResponseEntity.status(HttpStatus.OK).body(blogService.searchByKeyword(keyword));
    }
}
