package school.bonobono.fyb.domain.closet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.domain.closet.Entity.Closet;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClosetRepository extends JpaRepository<Closet, Long> {
}
