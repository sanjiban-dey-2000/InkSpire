package com.example.inkSpire.controller;

import com.example.inkSpire.dto.BlogDto;
import com.example.inkSpire.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("/blog/add")
    public ResponseEntity<?> addBlogs(@RequestBody BlogDto blogDto){
        try{
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
}
