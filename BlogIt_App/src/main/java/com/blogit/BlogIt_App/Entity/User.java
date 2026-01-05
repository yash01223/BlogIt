package com.blogit.BlogIt_App.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;


    private String username;
    private String email;
    private String password;
    private boolean enabled = true;


    private LocalDate createdAt;
    private LocalDate updatedAt;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();


    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
        @JsonManagedReference
        private List<Post> posts = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }


    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }


    public void removePost(Post post) {
        posts.remove(post);
        post.setUser(null);
    }
}

