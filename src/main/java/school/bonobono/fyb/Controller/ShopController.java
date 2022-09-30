package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.ShopDataDto;
import school.bonobono.fyb.Dto.ShopDto;
import school.bonobono.fyb.Entity.Shop;
import school.bonobono.fyb.Service.ShopService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("main")
public class ShopController {

    private final ShopService shopService;

    // Main 홈 페이지
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Object> getAllShopAndUserInfo(HttpServletRequest request) {
        return shopService.getAllShopAndUserInfo(request);
    }

    // 쇼핑몰 클릭시 쇼핑몰 이용자의 빅데이터 분석
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public HashMap<Object, Object> saveShopData(
            @RequestBody final ShopDataDto.Request request, HttpServletRequest headerRequest
    ) {
        return shopService.saveShopData(request, headerRequest);
    }

    // Search 페이지 Get
    @GetMapping("shop")
    public List<Shop> getAllShop() {
        return shopService.getAllShopInfo();
    }

    // Search 페이지 Post
    @PostMapping("shop")
    public List<Shop> getSearchShop(
            @RequestBody final ShopDto.Request request
    ) {
        return shopService.getSearchShop(request);
    }
}
