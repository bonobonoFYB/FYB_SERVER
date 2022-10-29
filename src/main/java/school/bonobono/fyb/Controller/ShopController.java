package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.ShopDataDto;
import school.bonobono.fyb.Dto.ShopDto;
import school.bonobono.fyb.Entity.Shop;
import school.bonobono.fyb.Service.ShopService;

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
    public List<Object> getAllShopAndUserInfo(
    ) {
        return shopService.getAllShopAndUserInfo();
    }

    // 사용자 최다조회수 API
    @GetMapping("rank/all")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ShopDto.Response>> getMostViewed(
    ) {
        return shopService.getMostViewed();
    }

    // 사용자 나이대별 최다조회수 API
    @GetMapping("rank/age")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ShopDto.Response>> getAgeViewed(
            @RequestParam(required = false, value = "value") Integer value
    ) {
        return shopService.getAgeViewed(value);
    }

    // 사용자 성별 최다조회수 API
    @GetMapping("rank/gender")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ShopDto.Response>> getGenderViewed(
            @RequestParam(required = false, value = "value") Character value
    ) {
        return shopService.getGenderViewed(value);
    }

    // 쇼핑몰 클릭시 쇼핑몰 이용자의 빅데이터 분석
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<HashMap<Object, Object>> saveShopData(
            @RequestBody final ShopDataDto.Request request
    ) {
        return shopService.saveShopData(request);
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
