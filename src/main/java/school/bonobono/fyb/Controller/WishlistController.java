package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.WishlistDto;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Service.WishlistService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.constant.Constable;
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
    public List<WishlistDto.Response> getWishlistInfo(
    ) {
        return wishlistService.getWishlistInfo();
    }

    // 회원 장바구니 등록
    @PostMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<StatusTrue> addWishlistInfo(
            @Valid @RequestBody WishlistDto.Request request
    ) {
        return wishlistService.addWishlistInfo(request);
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
            @Valid @RequestBody WishlistDto.Response request
    ) {
        return wishlistService.updateWishlistInfo(request);
    }
}
