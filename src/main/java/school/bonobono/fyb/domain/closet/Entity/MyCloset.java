package school.bonobono.fyb.domain.closet.Entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MyCloset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 논리적 설계의 FK ( 사용자 ID )
    private Long uid;
    private String pname;
    private String pkind;
    private String pnotes;
    @Column(name = "closetImagePath")
    private String closetImagePath;

}
