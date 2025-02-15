package org.springboot.event_horizon.utilities;

import org.springboot.event_horizon.entities.Role;
import org.springboot.event_horizon.entities.RoleName;
import org.springboot.event_horizon.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(RoleName.USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
        if (roleRepository.findByName(RoleName.CLUB_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.CLUB_ADMIN
            );
            roleRepository.save(adminRole);
        }
    }
}
