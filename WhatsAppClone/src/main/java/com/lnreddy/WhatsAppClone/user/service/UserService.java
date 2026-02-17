package com.lnreddy.WhatsAppClone.user.service;

import com.lnreddy.WhatsAppClone.user.dto.UserResponse;
import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.mapper.UserMapper;
import com.lnreddy.WhatsAppClone.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsersExceptSelf(Authentication connectedUser){
         return userRepository.findUsersExceptSelf(connectedUser.getName())
                 .stream()
                 .map(userMapper::toUserResponse)
                 .toList();
    }

    public User findByUserEmailId(String email){
          return userRepository.findByEmail(email).orElseThrow(
                  () -> new RuntimeException("User is not found")
          );
    }

}
