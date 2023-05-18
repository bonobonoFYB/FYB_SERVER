package school.bonobono.fyb.domain.shop.Entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "shop")
@EntityListeners(AuditingEntityListener.class)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    private String shopName;

    @NotNull
    private String shopUrl;

    @NotNull
    private String shopImage;

    @NotNull
    private Boolean shopData = false;

    public void updateShopData() {
        this.shopData = true;
    }
}
