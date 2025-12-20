package com.blogit.BlogIt_App.Service;


import com.blogit.BlogIt_App.Exception.UserNotFoundException;
import com.blogit.BlogIt_App.dto.UserDTO;
import com.blogit.BlogIt_App.Entity.User;
import com.blogit.BlogIt_App.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {


    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        User savedUser = userRepository.save(user);
        return toUserDTO(savedUser);
    }


    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
        return userDTOs;
    }


    @Transactional(readOnly = true)
    public UserDTO getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return toUserDTO(user);
    }


    @Transactional
    public UserDTO updateUser(int id, UserDTO updatedUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));


        user.setUsername(updatedUserDTO.getUsername());
        user.setEmail(updatedUserDTO.getEmail());
        user.setPassword(updatedUserDTO.getPassword());


        User savedUser = userRepository.save(user);
        return toUserDTO(savedUser);
    }


    @Transactional
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));


        userRepository.delete(user);
        return;
    }


    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Avoid initializing the lazy posts collection; just use size if loaded
        dto.setPostCount(user.getPosts() != null ? user.getPosts().size() : 0);

        return dto;
    }
}