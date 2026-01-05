package com.blogit.BlogIt_App.Service;


import com.blogit.BlogIt_App.Exception.UserNotFoundException;
import com.blogit.BlogIt_App.dto.UserDTO;
import com.blogit.BlogIt_App.Entity.User;
import com.blogit.BlogIt_App.Entity.Role;
import com.blogit.BlogIt_App.Repository.UserRepository;
import com.blogit.BlogIt_App.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {


    private final UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(true);
        
        // Assign default USER role
        Role userRole = roleRepository.findByRoleType(Role.RoleType.USER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleType(Role.RoleType.USER);
//                    role.setDescription("Standard user role");
                    return roleRepository.save(role);
                });
        
        user.getRoles().add(userRole);

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
        if (updatedUserDTO.getPassword() != null && !updatedUserDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
        }


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
    
    @Transactional
    public void assignRoleToUser(int userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        Role role = roleRepository.findByRoleType(Role.RoleType.valueOf(roleName))
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }


    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setEnabled(user.isEnabled());

        // Avoid initializing the lazy posts collection; just use size if loaded
        dto.setPostCount(user.getPosts() != null ? user.getPosts().size() : 0);
        
        // Map roles
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> role.getRoleType().toString())
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}