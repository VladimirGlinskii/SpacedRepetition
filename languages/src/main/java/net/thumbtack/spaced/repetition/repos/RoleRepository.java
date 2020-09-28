package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.Role;
import net.thumbtack.spaced.repetition.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(RoleName name);

}
