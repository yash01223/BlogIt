package com.blogit.BlogIt_App.Controller;


import com.blogit.BlogIt_App.dto.PostDTO;
import com.blogit.BlogIt_App.Service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/posts")
public class PostController {


    private final PostService postService;

    
    public PostController(PostService postService) {
        this.postService = postService;
    }


    // CREATE POST FOR USER
    @PostMapping("/user/{userId}")
    public ResponseEntity<PostDTO> createPost(
           @PathVariable int userId,
            @Valid @RequestBody PostDTO postDTO) {
        PostDTO created = postService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    // GET ALL POSTS
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> list = postService.getAllPosts();
        return ResponseEntity.ok(list);
    }


    // GET POST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable int id) {
        PostDTO dto = postService.getPostById(id);
        return ResponseEntity.ok(dto);
    }


    // GET POSTS BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUser(@PathVariable int userId) {
        List<PostDTO> list = postService.getPostsByUser(userId);
        return ResponseEntity.ok(list);
    }


    // UPDATE POST
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable int id,
            @Valid @RequestBody PostDTO postDTO) {
        PostDTO updated = postService.updatePost(id, postDTO);
        return ResponseEntity.ok(updated);
    }


    // DELETE POST
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}