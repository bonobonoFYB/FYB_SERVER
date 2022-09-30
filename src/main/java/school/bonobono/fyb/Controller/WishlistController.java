package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.WishlistDto;
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
    public List<WishlistDto.Response> getWishlistInfo(HttpServletRequest headerRequest) {
        return wishlistService.getWishlistInfo(headerRequest);
    }

    // 회원 장바구니 등록
    @PostMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable addWishlistInfo(@Valid @RequestBody WishlistDto.Request request, HttpServletRequest headerRequest) {
        return wishlistService.addWishlistInfo(request, headerRequest);
    }

    // 회원 장바구니 삭제
    @DeleteMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable deleteWishlistInfo(@Valid @RequestBody WishlistDto.deleteRequest request, HttpServletRequest headerRequest) {
        return wishlistService.deleteWishlistInfo(request, headerRequest);
    }

    // 회원 장바구니 수정
    @PatchMapping("wishlist")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Constable updateWishlistInfo(@Valid @RequestBody WishlistDto.Response request, HttpServletRequest headerRequest) {
        return wishlistService.updateWishlistInfo(request, headerRequest);
    }
}
