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
    @Column(name = "pid")
    private Long pid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private FybUser user;

    @Column(name = "pname",length = 50)
    private String pname;

    @Column(name = "pnotes")
    private String notes;

    @Column(name = "purl")
    private String purl;

    @Column(name = "price")
    private Integer price;

    @CreatedDate
    @Column(name = "createAt")
    private LocalDateTime pcreateAt;
    @LastModifiedDate
    @Column(name = "updateAt")
    private LocalDateTime pupdateAt;
}
