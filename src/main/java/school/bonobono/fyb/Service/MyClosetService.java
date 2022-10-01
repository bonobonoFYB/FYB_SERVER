package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.MyClosetDto;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Entity.MyCloset;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Repository.MyClosetRepository;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.constant.Constable;
import java.util.*;

import static school.bonobono.fyb.Exception.CustomErrorCode.*;
import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;
import static school.bonobono.fyb.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyClosetService {
    private final MyClosetRepository myClosetRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    // Validation 및 단순화
    private static void readMyClosetValidate(List<MyClosetDto.readResponse> list) {
        if (list.isEmpty()) {
            throw new CustomException(MY_CLOSET_EMPTY);
        }
    }

    private static void addMyClosetValidate(MyClosetDto.addRequest request) {
        if (request.getPname() == null) {
            throw new CustomException(MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPkind() == null) {
            throw new CustomException(MY_CLOSET_PKIND_IS_NULL);
        }
    }

    private static void updateValidate(MyClosetDto.readResponse request) {
        if (request.getId() == null) {
            throw new CustomException(MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPname() == null) {
            throw new CustomException(MY_CLOSET_PNAME_IS_NULL);
        }

        if (request.getPkind() == null) {
            throw new CustomException(MY_CLOSET_PKIND_IS_NULL);
        }

    }

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

    // Service

    @Transactional
    public List<MyClosetDto.readResponse> readMyCloset(HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        List<MyClosetDto.readResponse> list = myClosetRepository
                .findByUid(getTokenInfo().getId())
                .stream()
                .map(MyClosetDto.readResponse::Response).toList();

        readMyClosetValidate(list);

        return list;
    }

    @Transactional
    public List<Object> addMyCloset(MyClosetDto.addRequest request, HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        addMyClosetValidate(request);

        myClosetRepository.save(
                MyCloset.builder()
                        .uid(getTokenInfo().getId())
                        .pkind(request.getPkind())
                        .pname(request.getPname())
                        .pnotes(request.getPnotes())
                        .build()
        );

        MyCloset myCloset = myClosetRepository.findByPname(request.getPname()).orElseThrow(
                NullPointerException::new
        );

        HashMap<String,Long> response = new HashMap<>();
        response.put("id",myCloset.getId());

        List<Object> list = new ArrayList<>();
        list.add(response);
        list.add(MY_CLOSET_ADD_STATUS_TRUE);

        return list;
    }

    @Transactional
    public Constable deleteCloset(MyClosetDto.deleteRequest request, HttpServletRequest headerRequest) {
        tokenCredEntialsValidate(headerRequest);

        if (request.getId() == null) {
            throw new CustomException(MY_CLOSET_ID_IS_NULL);
        }

        myClosetRepository.deleteById(request.getId());

        return MY_CLOSET_DELETE_STATUS_TRUE;
    }

    @Transactional
    public Constable updateCloset(MyClosetDto.readResponse request, HttpServletRequest headerRequest) {

        updateValidate(request);

        myClosetRepository.save(
                MyCloset.builder()
                        .id(request.getId())
                        .uid(getTokenInfo().getId())
                        .pkind(request.getPkind())
                        .pnotes(request.getPnotes())
                        .pname(request.getPname())
                        .build()
        );

        return MY_CLOSET_UPDATE_STATUS_TRUE;
    }
}
