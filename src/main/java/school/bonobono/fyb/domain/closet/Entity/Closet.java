package school.bonobono.fyb.domain.closet.Entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import school.bonobono.fyb.domain.user.Entity.FybUser;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Closet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private FybUser user;

    private String productName;
    private String productKind;
    private String productNotes;
    @Column(name = "closetImagePath")
    private String closetImagePath;

    public void updateImagePath(String closetImagePath) {
        this.closetImagePath = closetImagePath;
    }

    public void updateCloset(String productName, String productNotes, String productKind) {
        this.productName = productName;
        this.productNotes = productNotes;
        this.productKind = productKind;
    }

}
