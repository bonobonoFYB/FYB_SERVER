package school.bonobono.fyb.global.exception;

import lombok.Getter;
import lombok.Setter;
import school.bonobono.fyb.global.model.Result;

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
