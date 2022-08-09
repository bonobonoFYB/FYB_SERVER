package school.bonobono.fyb.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.bonobono.fyb.Dto.ShopDto;
import school.bonobono.fyb.Entity.Shop;
import school.bonobono.fyb.Service.ShopService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class ShopController {

    private final ShopService shopService;

    // Main 홈 페이지
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Object> getAllShopAndUserInfo(HttpServletRequest request) {
        return shopService.getAllShopAndUserInfo();
    }

    // Search 페이지 Get
    @GetMapping("/search")
    public List<Shop> getAllShop(){
        return shopService.getAllShopInfo();
    }

    // Search 페이지 Post
    @PostMapping("/search")
    public List<Shop> getSearchShop(
             @RequestBody final ShopDto.Request request
    ){
        return shopService.getSearchShop(request);
    }
}
