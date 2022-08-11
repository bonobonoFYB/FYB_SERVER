package school.bonobono.fyb.Entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class userToken {

    @Id
    @NotNull
    @Column(name = "token")
    private String token;
}
