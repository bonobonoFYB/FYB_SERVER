package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.bonobono.fyb.Entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
}
