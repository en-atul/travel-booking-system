package com.proj.authservice.service.user;


import com.proj.authservice.dto.UserDto;
import com.proj.authservice.model.Role;
import com.proj.authservice.model.User;
import com.proj.authservice.repository.RoleRepository;
import com.proj.authservice.repository.UserRepository;
import com.proj.authservice.request.user.CreateUserReq;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(CreateUserReq request){
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        
        // Set the role
        Role role = roleRepository.findByName(request.role())
                .orElseGet(() -> {
                    Role newRole = new Role(request.role());
                    return roleRepository.save(newRole);
                });
        user.getRoles().add(role);
        
        return userRepository.save(user);
    }

    @Override
    public UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
    
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
