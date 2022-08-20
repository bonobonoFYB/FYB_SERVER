package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.bonobono.fyb.Dto.MyClosetDto;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Repository.MyClosetRepository;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static school.bonobono.fyb.Exception.CustomErrorCode.JWT_CREDENTIALS_STATUS_FALSE;
import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyClosetService {
    private final MyClosetRepository myClosetRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public List<MyClosetDto.readResponse> readMyCloset(HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        List<MyClosetDto.readResponse> list = myClosetRepository
                .findByUid(getTokenInfo().getId())
                .stream()
                .map(MyClosetDto.readResponse::Response).toList();
        return list;
    }

    // Validation 및 단순화

    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
        );
    }

    private void tokenCredEntialsValidate(HttpServletRequest request) {
        tokenRepository
                .findById(request.getHeader(AUTHORIZATION_HEADER))
                .orElseThrow(
                        () -> new CustomException(JWT_CREDENTIALS_STATUS_FALSE)
                );
    }
}
