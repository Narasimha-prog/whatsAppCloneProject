package com.lnreddy.WhatsAppClone.user.service;

import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import com.lnreddy.WhatsAppClone.user.dto.UserResponse;
import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.mapper.UserMapper;
import com.lnreddy.WhatsAppClone.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsersExceptSelf(Authentication connectedUser){

        CustomeUserDetails userDetails=(CustomeUserDetails) connectedUser.getPrincipal();

         return userRepository.findUsersExceptSelf(userDetails.getId())
                 .stream()
                 .map(userMapper::toUserResponse)
                 .toList();
    }

    public User findByUserEmailId(String email){
          return userRepository.findByEmail(email).orElseThrow(
                  () -> new RuntimeException("User is not found")
          );
    }

    public User findByUserId(UUID userId){
        return userRepository.findByPublicId(userId).orElseThrow(
                () -> new RuntimeException("User is not found")
        );
    }

}
