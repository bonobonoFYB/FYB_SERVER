package school.bonobono.fyb.domain.shop.Entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ShopData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 논리적 설계의 FK ( shop ID )
    private Long sid;

    @NotNull
    private Integer clickAll;

    @NotNull
    private Integer clickMen;

    @NotNull
    private Integer clickWomen;

    @NotNull
    private Integer clickAgeA;

    @NotNull
    private Integer clickAgeB;

    @Column(name = "shop", length = 15)
    @NotNull
    private String shop;

    @NotNull
    @Column(name = "shop_url")
    private String surl;

    @Column(name = "simg")
    @NotNull
    private String simg;

}
