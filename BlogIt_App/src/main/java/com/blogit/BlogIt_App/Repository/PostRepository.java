package com.blogit.BlogIt_App.Repository;

import com.blogit.BlogIt_App.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUser_Id(int userId);
}