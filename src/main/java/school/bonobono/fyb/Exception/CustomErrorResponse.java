package school.bonobono.fyb.Exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorResponse {
    private CustomErrorCode errorCode;
    private String errorMessage;

}
