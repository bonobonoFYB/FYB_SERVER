package school.bonobono.fyb.domain.wishlist.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.domain.wishlist.Dto.WishlistDto;
import school.bonobono.fyb.domain.wishlist.Service.WishlistService;
import school.bonobono.fyb.global.model.CustomResponseEntity;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

    // 회원 장바구니 조회
    @GetMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<List<WishlistDto.DetailDto>> getWishlistInfo(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        return CustomResponseEntity.success(wishlistService.getWishlistInfo(userDetails));
    }

    // 회원 장바구니 등록
    @PostMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<WishlistDto.SaveDto> addWishlistInfo(
            @Valid @RequestBody WishlistDto.SaveDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return CustomResponseEntity.success(wishlistService.addWishlistInfo(request, userDetails));
    }

    // 회원 장바구니 삭제
    @DeleteMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<WishlistDto.DetailDto> deleteWishlistInfo(
            @Valid @RequestBody WishlistDto.DeleteDto request
    ) {
        return CustomResponseEntity.success(wishlistService.deleteWishlistInfo(request));
    }

    // 회원 장바구니 수정
    @PatchMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomResponseEntity<WishlistDto.DetailDto> updateWishlistInfo(
            @Valid @RequestBody WishlistDto.UpdateDto request
    ) {
        return CustomResponseEntity.success(wishlistService.updateWishlistInfo(request));
    }
}
