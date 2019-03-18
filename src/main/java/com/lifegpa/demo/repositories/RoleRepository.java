package com.lifegpa.demo.repositories;


import com.lifegpa.demo.models.Role;
import com.lifegpa.demo.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
