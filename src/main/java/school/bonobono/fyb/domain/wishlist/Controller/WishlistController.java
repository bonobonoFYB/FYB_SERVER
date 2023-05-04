package school.bonobono.fyb.domain.wishlist.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.domain.wishlist.Dto.WishlistDto;
import school.bonobono.fyb.global.Model.StatusTrue;
import school.bonobono.fyb.domain.wishlist.Service.WishlistService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

/*    // 회원 장바구니 조회
    @GetMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<WishlistDto.Response> getWishlistInfo(
            ) {
        return wishlistService.getWishlistInfo();
    }*/

    // 회원 장바구니 등록
    @PostMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> addWishlistInfo(
            @Valid @RequestBody WishlistDto.Request request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return wishlistService.addWishlistInfo(request,userDetails);
    }

    // 회원 장바구니 삭제
    @DeleteMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> deleteWishlistInfo(
            @Valid @RequestBody WishlistDto.deleteRequest request
    ) {
        return wishlistService.deleteWishlistInfo(request);
    }

    // 회원 장바구니 수정
    @PatchMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> updateWishlistInfo(
            @Valid @RequestBody WishlistDto.Response request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return wishlistService.updateWishlistInfo(request, userDetails);
    }
}
