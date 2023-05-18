package school.bonobono.fyb.domain.shop.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.shop.Dto.ShopDto;
import school.bonobono.fyb.domain.shop.Entity.Shop;
import school.bonobono.fyb.domain.shop.Repository.ShopRepository;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;
import school.bonobono.fyb.global.redis.service.RedisService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final RedisService redisService;

    // Service
    // Main 홈 조회 페이지
    @Transactional
    public List<ShopDto.DetailListDto> getAllShop() {
        return shopRepository.findAll().stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    // 쇼핑몰 검색 (문자열 포함 기반)
    @Transactional
    public List<ShopDto.DetailListDto> getSearchShop(ShopDto.SearchDto request) {
        List<Shop> shopList = shopRepository.findByShopNameContaining(request.getShop());
        if (shopList.isEmpty()) {
            throw new CustomException(Result.SEARCH_EMPTY);
        }
        return shopList.stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopDto.SaveDto saveShopData(ShopDto.SaveDto request, FybUser user) {

        Shop shop = getShop(request.getShopId());
        Long shopId = shop.getId();

        // 매장의 초기 데이터가 저장되지 않은 상태인 경우
        if (shop.getShopData() == false) {
            redisService.saveInitialShopData(shopId);
            shop.updateShopData();
        }

        // 매장 조회수 데이터 수집
        redisService.saveShopData(shopId, "_viewCount");

        // 성별 조회수 데이터 수집
        if (user.getGender().equals('M')) {
            redisService.saveShopData(shopId, "_maleUserCount");
        } else if (user.getGender().equals('W')) {
            redisService.saveShopData(shopId, "_femaleUserCount");
        }

        // 나이 조회수 데이터 수집
        if (user.getAge() <= 29) {
            redisService.saveShopData(shopId, "_twenties");
        } else if (user.getAge() <= 39) {
            redisService.saveShopData(shopId, "_thirties");
        }

        return ShopDto.SaveDto.response(shop);
    }

    @Transactional
    public List<ShopDto.DetailListDto> getMostViewed() {
        List<Long> sortedViewsShopId = redisService.getSortedShopId("_viewCount");
        List<Shop> shops = shopRepository.findByIdIn(sortedViewsShopId);

        shops.sort(Comparator.comparing(shop -> sortedViewsShopId.indexOf(shop.getId())));

        return shops.stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShopDto.DetailListDto> getAgeViewed(FybUser user) {
        List<Long> sortedAgesShopId = new ArrayList<>();

        if (user.getAge() <= 29) {
            sortedAgesShopId = redisService.getSortedShopId("_twenties");
        } else if (user.getAge() <= 39) {
            sortedAgesShopId = redisService.getSortedShopId("_thirties");
        }

        return shopRepository.findByIdIn(sortedAgesShopId).stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShopDto.DetailListDto> getGenderViewed(FybUser user) {
        List<Long> sortedGendersShopId = new ArrayList<>();

        if (user.getGender() == 'M') {
            sortedGendersShopId = redisService.getSortedShopId("_maleUserCount");
        } else if (user.getGender() == 'W'){
            sortedGendersShopId = redisService.getSortedShopId("_femaleUserCount");
        }

        return shopRepository.findByIdIn(sortedGendersShopId).stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    private Shop getShop(Long id) {
        return shopRepository.findById(id).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_SHOP)
        );
    }

    private FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(
                email).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER)
        );
    }
}
