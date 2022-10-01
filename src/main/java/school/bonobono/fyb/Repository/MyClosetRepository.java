package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.Entity.MyCloset;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyClosetRepository extends JpaRepository<MyCloset, Long> {
    List<MyCloset> findByUid(Long uid);
    Optional<MyCloset> findByPname(String pname);
}
