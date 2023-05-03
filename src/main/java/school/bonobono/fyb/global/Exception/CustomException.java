package school.bonobono.fyb.global.Exception;

import lombok.Getter;
import lombok.Setter;
import school.bonobono.fyb.global.Model.Result;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private Result result;
    private String debug;

    public CustomException(Result result) {
        this.result = result;
        this.debug = result.getMessage();
    }
}
