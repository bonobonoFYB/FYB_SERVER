package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.bonobono.fyb.Entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
