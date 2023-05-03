package school.bonobono.fyb.global.Exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorResponse {
    private Integer status;
    private String statusMessage;

}
