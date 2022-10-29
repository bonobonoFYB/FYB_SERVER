package school.bonobono.fyb.domain.user.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.domain.user.Entity.FybUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<FybUser, Long> {
    @EntityGraph(attributePaths = "authorities")
    Optional<FybUser> findOneWithAuthoritiesByEmail(String email);

    Optional<FybUser> findByEmail(String email);

    Boolean existsByEmail(String email);
}
