package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.bonobono.fyb.Entity.userToken;

public interface TokenRepository extends JpaRepository<userToken,String> {

    boolean existsById(String token);
}
