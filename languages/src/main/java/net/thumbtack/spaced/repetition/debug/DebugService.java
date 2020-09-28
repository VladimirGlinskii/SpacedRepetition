package net.thumbtack.spaced.repetition.debug;

import net.thumbtack.spaced.repetition.model.User;
import net.thumbtack.spaced.repetition.model.enums.RoleName;
import net.thumbtack.spaced.repetition.repos.RoleRepository;
import net.thumbtack.spaced.repetition.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DebugService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public DebugService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public void clear() {
        userRepository.deleteAll();
    }

    public void makeAdmin(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.getRoles().add(roleRepository.findByName(RoleName.ROLE_ADMIN));
            userRepository.save(user);
        }

    }
}
