package com.proj.authservice.service.user;

import com.proj.authservice.dto.UserDto;
import com.proj.authservice.model.User;
import com.proj.authservice.request.user.CreateUserReq;


public interface IUserService {

    User createUser(CreateUserReq request);

    UserDto convertUserToDto(User user);

    User getUserById(Long userId);
    
    User getUserByEmail(String email);
}
