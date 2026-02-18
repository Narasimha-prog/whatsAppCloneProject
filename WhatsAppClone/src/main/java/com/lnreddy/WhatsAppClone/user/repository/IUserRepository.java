package com.lnreddy.WhatsAppClone.user.repository;

import com.lnreddy.WhatsAppClone.user.constants.UserConstants;
import com.lnreddy.WhatsAppClone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {

      @Query(name = UserConstants.FIND_USER_BY_EMAIL)
      Optional<User> findByEmail(@Param("email") String userMail);

      @Query(name = UserConstants.FIND_USER_BY_PUBLIC_ID)
      Optional<User>  findByPublicId(@Param("publicId") UUID publicId);

      @Query(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF)
      List<User> findUsersExceptSelf(@Param("publicId") UUID user);

      boolean existsByEmail(String email);

}
