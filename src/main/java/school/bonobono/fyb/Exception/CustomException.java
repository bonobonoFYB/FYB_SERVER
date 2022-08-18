package school.bonobono.fyb.Exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{ // runtimeException 상속
    private CustomErrorCode customErrorCode;
    private String detaliMessage;

    public CustomException(CustomErrorCode customErrorCode){
        super(customErrorCode.getMessage()); // runtimeException
        this.customErrorCode = customErrorCode;
        this.detaliMessage = customErrorCode.getMessage();
    }

    public CustomException(CustomErrorCode customErrorCode, String detaliMessage){
        super(detaliMessage); // runtimeException
        this.customErrorCode = customErrorCode;
        this.detaliMessage = detaliMessage;
    }
}
