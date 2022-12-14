package school.bonobono.fyb.domain.wishlist.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.user.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.domain.wishlist.Dto.WishlistDto;
import school.bonobono.fyb.domain.wishlist.Entity.Wishlist;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.domain.wishlist.Repository.WishlistRepository;
import school.bonobono.fyb.global.Config.Jwt.SecurityUtil;

import java.util.List;
import java.util.Objects;

import static school.bonobono.fyb.global.Exception.CustomErrorCode.*;
import static school.bonobono.fyb.global.Model.StatusTrue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    // Validation 및 단순화
    private TokenInfoResponseDto getTokenInfo() {
        return TokenInfoResponseDto.Response(
                Objects.requireNonNull(SecurityUtil.getCurrentUsername()
                        .flatMap(
                                userRepository::findOneWithAuthoritiesByEmail)
                        .orElse(null))
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
        if (request.getPid() == null)
            throw new CustomException(WISHLIST_PID_IS_NULL);
    }

    // Service
    // 사용자 장바구니 전체조회
    @Transactional
    public List<WishlistDto.Response> getWishlistInfo() {

        List<WishlistDto.Response> list = wishlistRepository
                .findByUid(getTokenInfo().getId())
                .stream()
                .map(WishlistDto.Response::response).toList();

        GET_WISHLIST_INFO_VALIDATION(list);

        return list;
    }

    // 사용자 장바구니 안 상품 등록
    @Transactional
    public ResponseEntity<StatusTrue> addWishlistInfo(WishlistDto.Request request) {
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
        return new ResponseEntity<>(WISHLIST_ADD_STATUS_TRUE, HttpStatus.OK);
    }

    // 사용자 장바구니 상품 삭제
    @Transactional
    public ResponseEntity<StatusTrue> deleteWishlistInfo(WishlistDto.deleteRequest request) {

        wishlistRepository.deleteById(request.getPid());
        return new ResponseEntity<>(WISHLIST_DELETE_STATUS_TRUE, HttpStatus.OK);
    }

    // 사용자 장바구니 상품 수정
    @Transactional
    public ResponseEntity<StatusTrue> updateWishlistInfo(WishlistDto.Response request) {
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
        return new ResponseEntity<>(WISHLIST_UPDATE_STATUS_TRUE, HttpStatus.OK);
    }
}
