package school.bonobono.fyb.Entity;

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

    @NotNull
    @Column(name = "shop_url")
    private String surl;


}
