package school.bonobono.fyb.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusFalse {
    JWT_CREDENTIALS_STATUS_FALSE("JWT_CREDENTIALS_STATUS_FALSE","로그인이 유효하지 않습니다."),
    PASSWORD_CHANGE_STATUS_FALSE("PASSWORD_CHANGE_STATUS_FALSE", "현재 비밀번호가 일치하지 않습니다." ),
    USER_DELETE_STATUS_FALSE("USER_DELETE_STATUS_FALSE", "비밀번호가 일치하지 않아 탈퇴에 실패했습니다." );


    private final String status;
    private final String statusMessage;
}
