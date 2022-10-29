package school.bonobono.fyb.domain.shop.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.domain.shop.Entity.ShopData;

@Repository
public interface ShopDataRepository extends JpaRepository<ShopData, Long> {
}
