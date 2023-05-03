package school.bonobono.fyb.global.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import school.bonobono.fyb.global.Model.CustomResponseEntity;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionHandler {

    // BAD_REQUEST 예외처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    public CustomErrorResponse handleException(
            CustomException e,
            HttpServletRequest request
    ) {
        log.error("errorCode : {}, url {}, message: {}",
                e.getResult().getCode(), request.getRequestURI(), e.getResult().getMessage());

        return CustomErrorResponse.builder()
                .status(e.getResult().getCode())
                .statusMessage(e.getResult().getMessage())
                .build();
    }

    // 예외처리 하기 힘든 예외처리
    @ExceptionHandler(value = {
            HttpRequestMethodNotSupportedException.class, // get, post 등 메소드(요청)가 매치하지 않았을경우
    })
    public CustomErrorResponse handleBadRequest(
            Exception e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getMessage());

        return CustomErrorResponse.builder()
                .status(-1)
                .statusMessage("is NotSupported HttpRequestMethod")
                .build();
    }

    // 알수없거나 알아내기 힘들 오류의 최후 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorResponse handleException(
            Exception e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getMessage());

        return CustomErrorResponse.builder()
                .status(-1)
                .statusMessage("Internal Server Error")
                .build();
    }

    // @Valid 예외처리
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return CustomResponseEntity.builder()
                .code(-1)
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();
    }

    // RequestParameter 예외처리
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MissingServletRequestParameterException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MissingServletRequestParameterException e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getParameterName() + " 값이 등록되지 않았습니다.");
        return CustomResponseEntity.builder()
                .code(-1)
                .message(e.getParameterName() + " 값이 등록되지 않았습니다.")
                .build();
    }

    // RequestPart 예외처리
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MissingServletRequestPartException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MissingServletRequestPartException e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getRequestPartName() + " 값을 요청받지 못했습니다.");
        return CustomResponseEntity.builder()
                .code(-1)
                .message("{ " + e.getRequestPartName() + " }"+ " 값을 요청받지 못했습니다.")
                .build();
    }

    // RequestHeader 예외처리
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MissingRequestHeaderException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MissingRequestHeaderException e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getHeaderName() + " 값을 요청받지 못했습니다.");
        return CustomResponseEntity.builder()
                .code(-1)
                .message("{ " + e.getHeaderName() + " }"+ " 값을 요청받지 못했습니다.")
                .build();
    }
}
