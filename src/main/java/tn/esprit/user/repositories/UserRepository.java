package tn.esprit.user.repositories;

import tn.esprit.user.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.Role;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserById(String id);
    User findUserByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findUsersByRoles(List<Role> roles);

    List<User> findByRolesContains(Role role);
    List<User> findUsersByIdAndRolesContainsAndProfileName(String id, Role role, String name);
    User findUserByIdAndRolesContainsAndProfileName(String id, Role role, String name);

}
