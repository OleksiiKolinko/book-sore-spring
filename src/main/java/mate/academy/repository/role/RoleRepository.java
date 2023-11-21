package mate.academy.repository.role;

import java.util.Set;
import mate.academy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findRoleByName(Role.RoleName roleName);
}
