package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.Entity.userToken;

@Repository
public interface TokenRepository extends JpaRepository<userToken,String> {
    boolean existsById(String token);
}
