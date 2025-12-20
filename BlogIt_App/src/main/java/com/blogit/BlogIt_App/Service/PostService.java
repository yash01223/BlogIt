package com.blogit.BlogIt_App.Service;


import com.blogit.BlogIt_App.Entity.Post;
import com.blogit.BlogIt_App.Entity.User;
import com.blogit.BlogIt_App.Exception.PostNotFoundException;
import com.blogit.BlogIt_App.Exception.UserNotFoundException;
import com.blogit.BlogIt_App.Repository.PostRepository;
import com.blogit.BlogIt_App.Repository.UserRepository;
import com.blogit.BlogIt_App.dto.PostDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostService {


    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


    /**
     * CREATE POST - Creates a new post for a user
     */
    @Transactional
    public PostDTO createPost(PostDTO postDTO, Integer userId) {
        // 1. Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + postDTO.getUserId()));

        // 2. Create the Post entity
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        // 3. Use the helper method (This is the fix!)
        // This calls post.setUser(user) AND user.getPosts().add(post)
        user.addPost(post);

        // 4. Save
        Post savedPost = postRepository.save(post);

        return toPostDTO(savedPost);
    }


    /**
     * GET ALL POSTS - Retrieves all posts
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostDTO> postDTOs = posts.stream()
                .map(this::toPostDTO)
                .collect(Collectors.toList());
        return postDTOs;
    }


    /**
     * GET POST BY ID - Retrieves a post by its ID
     */
    @Transactional(readOnly = true)
    public PostDTO getPostById(int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        return toPostDTO(post);
    }


    /**
     * GET POSTS BY USER - Retrieves all posts by a specific user
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsByUser(int userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));


        List<Post> posts = postRepository.findByUser_Id(userId);
        List<PostDTO> postDTOs = posts.stream()
                .map(this::toPostDTO)
                .collect(Collectors.toList());
        return postDTOs;
    }


    /**
     * UPDATE POST - Updates an existing post
     */
    @Transactional
    public PostDTO updatePost(int id, PostDTO updatedPostDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));


        post.setTitle(updatedPostDTO.getTitle());
        post.setContent(updatedPostDTO.getContent());


        Post savedPost = postRepository.save(post);
        return toPostDTO(savedPost);
    }


    /**
     * DELETE POST - Deletes a post by its ID
     */
    @Transactional
    public void deletePost(int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        postRepository.delete(post);
        return;
    }


    private PostDTO toPostDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        // Safely access lazy-loaded user within transactional context
        if (post.getUser() != null) {
            dto.setUserId(post.getUser().getId());
            dto.setUsername(post.getUser().getUsername());
        }

        return dto;
    }
}