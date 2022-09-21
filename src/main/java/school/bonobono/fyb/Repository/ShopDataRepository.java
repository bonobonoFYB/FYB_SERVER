package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.bonobono.fyb.Entity.MyCloset;
import school.bonobono.fyb.Entity.ShopData;

import java.util.Optional;

public interface ShopDataRepository extends JpaRepository<ShopData, Long> {
    Optional<MyCloset> findBySid(Long sid);
}
