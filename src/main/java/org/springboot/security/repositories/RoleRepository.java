package org.springboot.security.repositories;

import org.springboot.security.entities.Role;
import org.springboot.security.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
