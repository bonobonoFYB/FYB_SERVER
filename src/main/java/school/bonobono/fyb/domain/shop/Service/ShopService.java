package school.bonobono.fyb.domain.shop.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.shop.Dto.ShopDto;
import school.bonobono.fyb.domain.shop.Entity.Shop;
import school.bonobono.fyb.domain.shop.Repository.ShopRepository;
import school.bonobono.fyb.domain.user.Entity.FybUser;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.config.Redis.RedisDao;
import school.bonobono.fyb.global.exception.CustomException;
import school.bonobono.fyb.global.model.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final RedisDao redisDao;

    // Service
    // Main 홈 조회 페이지
    @Transactional
    public List<ShopDto.DetailListDto> getAllShop() {
        return shopRepository.findAll().stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    // Search 페이지 검색 (문자열 포함 기반)
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
    public ShopDto.SaveDto saveShopData(ShopDto.SaveDto request, UserDetails userDetails) {

        FybUser user = getUser(userDetails.getUsername());
        Shop shop = getShop(request.getShopId());
        Long shopId = shop.getId();

        // 매장의 초기 데이터가 저장되지 않은 상태인 경우
        if (shop.getShopData() == false) {
            redisDao.setValues(shopId + "_viewCount", "0");
            redisDao.setValues(shopId + "_maleUserCount", "0");
            redisDao.setValues(shopId + "_femaleUserCount", "0");
            redisDao.setValues(shopId + "_twenties", "0");
            redisDao.setValues(shopId + "_thirties", "0");
            shop.updateShopData();
        }

        // 매장 조회수 데이터 수집
        saveShopDataToRedis(shopId, "_viewCount");

        // 성별 조회수 데이터 수집
        if (user.getGender().equals('M')) {
            saveShopDataToRedis(shopId, "_maleUserCount");
        } else if (user.getGender().equals('W')) {
            saveShopDataToRedis(shopId, "_femaleUserCount");
        }

        // 나이 조회수 데이터 수집
        if (user.getAge() <= 29) {
            saveShopDataToRedis(shopId, "_twenties");
        } else if (user.getAge() <= 39) {
            saveShopDataToRedis(shopId, "_thirties");
        }

        return ShopDto.SaveDto.response(shop);
    }

    @Transactional
    public List<ShopDto.DetailListDto> getMostViewed() {
        List<Long> sortedViewsShopId = getSortedShopId("_viewCount");
        System.out.println(sortedViewsShopId);
        return shopRepository.findByIdsInSpecifiedOrder(sortedViewsShopId).stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShopDto.DetailListDto> getAgeViewed(UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());
        List<Long> sortedAgesShopId = new ArrayList<>();

        if (user.getAge() <= 29) {
            sortedAgesShopId = getSortedShopId("_twenties");
        } else if (user.getAge() <= 39) {
            sortedAgesShopId = getSortedShopId("_thirties");
        }

        return shopRepository.findByIdIn(sortedAgesShopId).stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ShopDto.DetailListDto> getGenderViewed(UserDetails userDetails) {
        FybUser user = getUser(userDetails.getUsername());
        List<Long> sortedGendersShopId = new ArrayList<>();

        if (user.getGender() == 'M') {
            sortedGendersShopId = getSortedShopId("_maleUserCount");
        } else {
            sortedGendersShopId = getSortedShopId("_femaleUserCount");
        }

        return shopRepository.findByIdIn(sortedGendersShopId).stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    private List<Long> getSortedShopId(String pattern) {
        Set<String> keys = redisDao.getKeys("*" + pattern);
        return keys.stream().sorted((a, b) -> {
            String viewsA = redisDao.getValues(a);
            String viewsB = redisDao.getValues(b);
            return Integer.parseInt(viewsB) - Integer.parseInt(viewsA);
        }).map(key -> Long.parseLong(key.substring(0, key.indexOf(pattern)))).toList();
    }

    private Shop getShop(Long id) {
        return shopRepository.findById(id).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_SHOP)
        );
    }

    private void saveShopDataToRedis(Long id, String keyName) {
        int incrementedCount = Integer.parseInt(redisDao.getValues(id + keyName));
        redisDao.setValues(id + keyName, String.valueOf(++incrementedCount));
    }

    private FybUser getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(
                email).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER)
        );
    }
}
