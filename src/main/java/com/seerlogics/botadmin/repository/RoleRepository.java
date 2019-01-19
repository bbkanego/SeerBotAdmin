package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bkane on 11/3/18.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
