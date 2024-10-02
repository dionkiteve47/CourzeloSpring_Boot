package tn.esprit.user.repositories;

import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserById(String id);
    List<User> findByEducationStclass(Class classe);
    List<User> findByEducationInstitution(Institution institution);
    User findUserByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findUsersByRoles(List<Role> roles);
    List<User> findByRolesIs(@NotNull List<Role> roles);
    List<User> findUsersByIdAndRolesIsAndProfileName(String id, @NotNull List<Role> roles, @NotNull String name);
    User findUserByIdAndRolesIsAndProfileName(String id, @NotNull List<Role> roles, @NotNull String name);

    List<User> findByRolesContains(Role role);
    List<User> findUsersByIdAndRolesContainsAndProfileName(String id, Role role, String name);
    User findUserByIdAndRolesContainsAndProfileName(String id, Role role, String name);
    default List<User> searchByKeyword(String keyword,int page, MongoTemplate mongoTemplate) {
        PageRequest pageRequest = PageRequest.of(page, 8);
        Query query = TextQuery.queryText(new TextCriteria().matching(keyword)).sortByScore().with(
                pageRequest
        );
        return mongoTemplate.find(query, User.class);
    }
}