package school.bonobono.fyb.global.Model;


import lombok.Getter;

@Getter
public enum Result {
    OK(0, "성공"),
    FAIL(-1, "실패"),

    //로그인 & 로그아웃 검증
    JWT_CREDENTIALS_STATUS_FALSE(-1001, "로그인이 유효하지 않습니다."),
    JWT_TIMEOUT(-1002, "만료된 JWT 토큰입니다."),
    LOGIN_FALSE(-1003, "아이디 또는 비밀번호를 잘못 입력하였습니다."),
    REFRESH_TOKEN_IS_BAD_REQUEST(-1004, "잘못된 RefreshToken 입니다 : null 이거나  not equals"),
    NOT_SOCIAL_LOGIN(-1005, "소셜 아이디로 회원가입된 유저입니다."),

    //회원가입
    REGISTER_INFO_NULL(-1006,"필수 항목을 입력하지 않았습니다."),
    PASSWORD_SIZE_ERROR(-1007,"비밀번호가 6자리 이상이여야 합니다."),
    NOT_EMAIL_FORM(-1008, "이메일 형식이 아닙니다."),
    NOT_CONTAINS_EXCLAMATIONMARK(-1009,"비밀번호에 특수문자가 포함되어있지 않습니다."),
    DUPLICATE_USER(-1010, "해당 이메일의 가입자가 이미 존재합니다."),

    // 업데이트
    UPDATE_INFO_NULL(-1011,"필수 항목을 입력하지 않았습니다."),

    // 비밀번호 변경
    PASSWORD_IS_NOT_CHANGE(-1022, "현재 사용하고있는 비밀번호로는 변경이 불가합니다."),
    PASSWORD_CHANGE_STATUS_FALSE(-1023, "현재 비밀번호가 일치하지 않습니다."),
    NOT_FOUND_USER(-1024, "해당 이메일의 유저가 존재하지 않습니다"),

    // 회원탈퇴
    USER_DELETE_STATUS_FALSE(-1025, "비밀번호가 일치하지 않아 탈퇴에 실패했습니다."),

    // 핸드폰 인증
    PHONE_NUM_ERROR(-1026, "핸드폰 번호가 잘못되었습니다."),

    // 검색화면
    SEARCH_EMPTY(-2001, "검색 항목이 존재하지 않습니다."),

    // 장바구니
    WISHLIST_EMPTY(-3001, "장바구니가 비어있습니다."),
    WISHLIST_PNAME_IS_NULL(-3002,"상품 이름을 입력하지 않았습니다."),
    WISHLIST_PURL_IS_NULL(-3003,"상품을 판매하는 주소를 입력하지 않았습니다."),
    WISHLIST_PID_IS_NULL(-3004, "PID 값이 넘어오지 않았습니다. (웹 / 서버 오류)"),
    NOT_FOUND_WISHLIST(-3005,"상품이 존재하지 않습니다."),

    // 내 옷장
    MY_CLOSET_EMPTY(-4001, "내 옷장이 비어있습니다."),
    MY_CLOSET_PNAME_IS_NULL(-4002,"등록하려는 옷의 이름이 작성되지 않았습니다."),
    MY_CLOSET_PKIND_IS_NULL(-4003, "등록하려는 옷의 종류가 선택되지 않았습니다."),
    MY_CLOSET_ID_IS_NULL(-4004, "아무 항목도 삭제하지 않습니다."),
    MY_CLOSET_DATA_ID_IS_NULL(-4005, "프론트, 서버오류 : 유저의 Data Id 값이 넘어오지 않았습니다."),

    // 이미지 관련
    IMAGE_UPLOAD_FAIL(-5001,"이미지 업로드에 실패하였습니다."),

    // 매장 관련
    NOT_FOUND_SHOP(-6001, "해당 매장은 존재하지 않습니다"),

    // 알수 없는 오류의 처리
    INTERNAL_SERVER_ERROR(-1,"서버에 오류가 발생했습니다."),
    INVALID_REQUEST(-1, "잘못된 요청입니다.");

    private final int code;
    private final String message;

    Result(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
