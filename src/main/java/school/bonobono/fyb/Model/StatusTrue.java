package school.bonobono.fyb.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusTrue {
    REGISTER_STATUS_TRUE(200,"회원가입 성공"),
    LOGIN_STATUS_TRUE(200,"로그인 성공"),
    READ_STATUS_TRUE(200,"정보 불러오기 성공"),
    UPDATE_STATUS_TURE(200, "회원정보 업데이트 성공");

    private final Integer status;
    private final String statusMessage;
}
