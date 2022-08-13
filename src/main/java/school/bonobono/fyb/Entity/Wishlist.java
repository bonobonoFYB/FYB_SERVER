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
    @Column(name = "pid")
    private Long pid;

    @Column(name = "uid")
    @NotNull
    private Long uid;

    @Column(name = "pname",length = 50)
    @NonNull
    private String pname;

    @Column(name = "pnotes")
    private String notes;

    @Column(name = "purl")
    @NotNull
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
