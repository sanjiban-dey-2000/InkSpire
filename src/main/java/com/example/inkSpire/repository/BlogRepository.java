package com.example.inkSpire.repository;

import com.example.inkSpire.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Long> {
    List<Blog> findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(String title,String body);
}
