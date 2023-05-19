package school.bonobono.fyb.domain.wishlist.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import school.bonobono.fyb.domain.user.Entity.FybUser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private FybUser user;

    private String productName;

    private String productNotes;

    private String productUrl;

    private Integer productPrice;

    @CreatedDate
    private LocalDateTime createAt;
    @LastModifiedDate
    private LocalDateTime updateAt;

    public void updateWishlist(String productName, String productNotes, Integer productPrice, String productUrl) {
        this.productName = productName;
        this.productUrl = productUrl;
        this.productNotes = productNotes;
        this.productPrice = productPrice;
    }
}
