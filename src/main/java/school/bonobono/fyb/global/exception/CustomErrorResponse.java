package school.bonobono.fyb.global.exception;

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
