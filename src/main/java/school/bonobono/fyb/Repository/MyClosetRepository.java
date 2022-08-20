package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.bonobono.fyb.Entity.MyCloset;

import java.util.Optional;

public interface MyClosetRepository extends JpaRepository<MyCloset,Long> {

    Optional<MyCloset> findByUid(Long uid);
}
