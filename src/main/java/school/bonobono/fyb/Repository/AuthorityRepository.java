package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.Entity.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
