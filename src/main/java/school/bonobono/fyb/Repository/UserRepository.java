package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import school.bonobono.fyb.Entity.FybUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<FybUser,Long> {
    @EntityGraph(attributePaths = "authorities")
    Optional<FybUser> findOneWithAuthoritiesByEmail(String email);

    Optional<FybUser> findByEmail(String email);

    Boolean existsByEmail(String email);

}
