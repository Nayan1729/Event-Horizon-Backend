package org.springboot.security.repositories;

import org.springboot.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserDetailsRepository extends CrudRepository<User,String> {

   public User findByUsername(String username);

   User findByVerificationToken(String token);

    boolean existsByUsername(String username);
}
