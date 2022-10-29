package school.bonobono.fyb.domain.closet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.domain.closet.Entity.MyCloset;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyClosetRepository extends JpaRepository<MyCloset, Long> {
    List<MyCloset> findByUid(Long uid);
    Optional<MyCloset> findByPnameAndUid(String pname, Long uid);
}
