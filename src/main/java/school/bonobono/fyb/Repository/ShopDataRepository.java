package school.bonobono.fyb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.Entity.ShopData;

@Repository
public interface ShopDataRepository extends JpaRepository<ShopData, Long> {
}
