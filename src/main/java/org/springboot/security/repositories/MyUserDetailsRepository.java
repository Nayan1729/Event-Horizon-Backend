package org.springboot.security.repositories;

import org.springboot.security.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserDetailsRepository extends CrudRepository<User,String> {

   public User findByEmail(String email);

   User findByVerificationToken(String token);

    boolean existsByEmail(String email);
}
