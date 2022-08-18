package school.bonobono.fyb.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusTrue {
    REGISTER_STATUS_TRUE(200, "회원가입 성공"),
    LOGIN_STATUS_TRUE(200, "로그인 성공"),
    READ_STATUS_TRUE(200, "정보 불러오기 성공"),
    LOGOUT_STATUS_TRUE(200, "로그아웃 성공"),
    UPDATE_STATUS_TURE(200, "회원정보 업데이트 성공"),
    WISHLIST_ADD_STATUS_TRUE(200, "장바구니 등록 성공"),
    WISHLIST_DELETE_STATUS_TRUE(200, "장바구니 삭제 성공"),
    WISHLIST_UPDATE_STATUS_TRUE(200, "장바구니 업데이트 성공"),
    PHONE_CHECK_STATUS_TRUE(200, "휴대폰 인증번호 보내기 성공"),
    PASSWORD_CHANGE_STATUS_TRUE(200, "비밀번호 변경 성공"),
    USER_DELETE_STATUS_TRUE(200, "회원탈퇴 성공");

    private final Integer status;
    private final String statusMessage;
}
