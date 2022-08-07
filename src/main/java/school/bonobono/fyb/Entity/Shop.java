package school.bonobono.fyb.Entity;

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
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_id")
    @NotNull
    private Long sid;

    @Column(name = "shop", length = 15)
    @NotNull
    private String shop;

    @Column(name = "shop_url")
    @NotNull
    private String surl;
}
