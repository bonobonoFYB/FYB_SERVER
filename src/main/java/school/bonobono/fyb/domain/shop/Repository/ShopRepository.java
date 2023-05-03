package school.bonobono.fyb.domain.shop.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.bonobono.fyb.domain.shop.Entity.Shop;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByShopNameContaining(String shopName);
}
