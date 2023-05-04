package school.bonobono.fyb.domain.wishlist.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.user.Dto.TokenInfoResponseDto;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.wishlist.Dto.WishlistDto;
import school.bonobono.fyb.domain.wishlist.Entity.Wishlist;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.Result;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.domain.wishlist.Repository.WishlistRepository;
import school.bonobono.fyb.global.Config.Jwt.SecurityUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

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

    private void GET_WISHLIST_INFO_VALIDATION(List<Wishlist> list) {
        if (list.isEmpty())
            throw new CustomException(Result.WISHLIST_EMPTY);
    }

    private void ADD_WISHLIST_INFO_VALIDATION(WishlistDto.SaveDto request) {
        if (request.getProductName() == null)
            throw new CustomException(Result.WISHLIST_PNAME_IS_NULL);
        if (request.getProductUrl() == null)
            throw new CustomException(Result.WISHLIST_PURL_IS_NULL);
    }
    private void UPDATE_WISHLIST_INFO_VALIDATION(WishlistDto.UpdateDto request) {
        if (request.getProductName() == null)
            throw new CustomException(Result.WISHLIST_PNAME_IS_NULL);
        if (request.getProductUrl() == null)
            throw new CustomException(Result.WISHLIST_PURL_IS_NULL);
        if (request.getId() == null)
            throw new CustomException(Result.WISHLIST_PID_IS_NULL);
    }

    private FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(
                email).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER)
        );
    }

    // Service
    // 사용자 장바구니 전체조회
    @Transactional
    public List<WishlistDto.DetailDto> getWishlistInfo(UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());
        List<Wishlist> wishlists = user.getWishlists();

        GET_WISHLIST_INFO_VALIDATION(wishlists);

        return wishlists.stream().map(WishlistDto.DetailDto::response).toList();
    }

    // 사용자 장바구니 안 상품 등록
    @Transactional
    public WishlistDto.SaveDto addWishlistInfo(WishlistDto.SaveDto request, UserDetails userDetails) {
        ADD_WISHLIST_INFO_VALIDATION(request);

        FybUser user = getUser(userDetails.getUsername());

        Wishlist wishlist = wishlistRepository.save(
                Wishlist.builder()
                        .user(user)
                        .productName(request.getProductName())
                        .productUrl(request.getProductUrl())
                        .productNotes(request.getProductNotes())
                        .productPrice(request.getProductPrice())
                        .build()
        );

        return WishlistDto.SaveDto.response(wishlist);
    }

    // 사용자 장바구니 상품 삭제
    @Transactional
    public WishlistDto.DetailDto deleteWishlistInfo(WishlistDto.DeleteDto request) {
        Wishlist wishlist = getWishlist(request.getId());
        wishlistRepository.delete(wishlist);
        return WishlistDto.DetailDto.response(wishlist);
    }

    // 사용자 장바구니 상품 수정
    @Transactional
    public WishlistDto.DetailDto updateWishlistInfo(WishlistDto.UpdateDto request) {
        UPDATE_WISHLIST_INFO_VALIDATION(request);

        Wishlist wishlist = getWishlist(request.getId());
        wishlist.updateWishlist(
                request.getProductName(),request.getProductNotes(),
                request.getProductNotes(), request.getProductPrice()
        );

        return WishlistDto.DetailDto.response(wishlist);
    }

    private Wishlist getWishlist(Long id) {
        return wishlistRepository.findById(id).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_WISHLIST)
        );
    }
}
