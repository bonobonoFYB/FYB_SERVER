package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.Dto.WishlistDto;
import school.bonobono.fyb.Entity.Wishlist;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Repository.WishlistRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.constant.Constable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static school.bonobono.fyb.Exception.CustomErrorCode.*;
import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;
import static school.bonobono.fyb.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {
    private final TokenRepository tokenRepository;

    private final WishlistRepository wishlistRepository;

    private final UserRepository userRepository;


    // Service
    // 사용자 장바구니 전체조회
    @Transactional
    public List<WishlistDto.Response> getWishlistInfo(HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        List<WishlistDto.Response> list = wishlistRepository
                .findByUid(getTokenInfo().getId())
                .stream()
                .map(WishlistDto.Response::response)
                .collect(
                        Collectors.toList()
                );

        GET_WISHLIST_INFO_VALIDATION(list);

        return list;
    }

    // 사용자 장바구니 안 상품 등록
    public Constable addWishlistInfo(WishlistDto.Request request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);
        ADD_WISHLIST_INFO_VALIDATION(request);

        wishlistRepository.save(
                Wishlist.builder()
                        .uid(getTokenInfo().getId())
                        .pname(request.getPname())
                        .purl(request.getPurl())
                        .notes(request.getNotes())
                        .price(request.getPrice())
                        .build()
        );
        return WISHLIST_ADD_STATUS_TRUE;
    }

    // 사용자 장바구니 상품 삭제
    public Constable deleteWishlistInfo(WishlistDto.deleteRequest request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        wishlistRepository.deleteById(request.getPid());
        return WISHLIST_DELETE_STATUS_TRUE;
    }

    // 사용자 장바구니 상품 수정
    public Constable updateWishlistInfo(WishlistDto.Response request, HttpServletRequest headerRequest) {
        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);
        UPDATE_WISHLIST_INFO_VALIDATION(request);

        wishlistRepository.save(
                Wishlist.builder()
                        .pid(request.getPid())
                        .uid(getTokenInfo().getId())
                        .pname(request.getPname())
                        .purl(request.getPurl())
                        .notes(request.getNotes())
                        .price(request.getPrice())
                        .build()
        );
        return WISHLIST_UPDATE_STATUS_TRUE;
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
    private void GET_WISHLIST_INFO_VALIDATION(List<WishlistDto.Response> list) {
        if (list.isEmpty())
            throw new CustomException(WISHLIST_EMPTY);
    }
    private void ADD_WISHLIST_INFO_VALIDATION(WishlistDto.Request request) {
        if (request.getPname() == null)
            throw new CustomException(WISHLIST_PNAME_IS_NULL);
        if (request.getPurl() == null)
            throw new CustomException(WISHLIST_PURL_IS_NULL);
    }

    private void UPDATE_WISHLIST_INFO_VALIDATION(WishlistDto.Response request) {
        if (request.getPname() == null)
            throw new CustomException(WISHLIST_PNAME_IS_NULL);
        if (request.getPurl() == null)
            throw new CustomException(WISHLIST_PURL_IS_NULL);
        if(request.getPid() == null)
            throw new CustomException(WISHLIST_PID_IS_NULL);
    }
}
