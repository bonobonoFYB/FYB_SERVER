package school.bonobono.fyb.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @Column(name = "p_id")
    @NotNull
    private Long pid;

    @ManyToOne(targetEntity = FybUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private Long uid;

    @Column(name = "p_name",length = 50)
    @NonNull
    private String pname;

    @Column(name = "p_notes")
    private String notes;

    @Column(name = "p_url")
    @NotNull
    private String purl;

    @Column(name = "p_price")
    private Integer price;

    @CreatedDate
    @Column(name = "p_createAt")
    private LocalDateTime pcreateAt;
    @LastModifiedDate
    @Column(name = "p_updateAt")
    private LocalDateTime pupdateAt;
}
