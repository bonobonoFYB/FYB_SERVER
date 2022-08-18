package school.bonobono.fyb.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // enum에 하나의 프로퍼티가 있는데 프로퍼티를 사용해서 enum을 하나 생성해줄때 메세지를 넣어서 생성해주는 생성자가 없어서 선언해줘야함
@Getter
public enum CustomErrorCode {
    SEARCH_EMPTY("검색 항목이 존재하지 않습니다."),
    JWT_CREDENTIALS_STATUS_FALSE("로그인이 유효하지 않습니다."),
    PASSWORD_CHANGE_STATUS_FALSE( "현재 비밀번호가 일치하지 않습니다." ),
    USER_DELETE_STATUS_FALSE( "비밀번호가 일치하지 않아 탈퇴에 실패했습니다." ),
    LOGIN_FALSE("아이디 또는 비밀번호를 잘못 입력하였습니다."),
    NOT_FOUND_USER("해당 이메일의 유저가 존재하지 않습니다"),
    // 알수 없는 오류의 처리
    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다.");

    private final String statusMessage;
}
