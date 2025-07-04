package com.lnreddy.WhatsAppClone.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    @Query(name = UserConstants.FIND_USER_BY_EMAIL)
    Optional<User> findByEmail(@Param("email") String userMail);

      @Query(name = UserConstants.FIND_USER_BY_PUBLIC_ID)
      Optional<User>  findByPublicId(@Param("publicId") String publicId);

      @Query(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF)
    List<User> findUsersExceptSelf(@Param("publicId") String user);
}
