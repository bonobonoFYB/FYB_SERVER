package school.bonobono.fyb.domain.shop.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.domain.shop.Dto.ShopDataDto;
import school.bonobono.fyb.domain.shop.Dto.ShopDto;
import school.bonobono.fyb.domain.shop.Entity.Shop;
import school.bonobono.fyb.domain.shop.Entity.ShopData;
import school.bonobono.fyb.domain.shop.Repository.ShopDataRepository;
import school.bonobono.fyb.domain.shop.Repository.ShopRepository;
import school.bonobono.fyb.domain.user.Repository.UserRepository;
import school.bonobono.fyb.global.Exception.CustomException;
import school.bonobono.fyb.global.Model.Result;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopDataRepository shopDataRepository;


    // Service
    // Main 홈 조회 페이지
    @Transactional
    public List<ShopDto.DetailListDto> getAllShopAndUserInfo() {
        return shopRepository.findAll().stream()
                .map(ShopDto.DetailListDto::response)
                .collect(Collectors.toList());
    }

    // Search 페이지 전체 조회
    @Transactional
    public List<Shop> getAllShopInfo() {
        return shopRepository.findAll();
    }

    // Search 페이지 검색 (문자열 포함 기반)
    @Transactional
    public List<Shop> getSearchShop(ShopDto.Request request) {
        List<Shop> shopList = shopRepository.findByShopNameContaining(request.getShop());
        if (shopList.isEmpty()) {
            throw new CustomException(Result.SEARCH_EMPTY);
        }
        return shopList;
    }

    // 쇼핑몰 사용자 데이터 저장
    @Transactional
    public ResponseEntity<HashMap<Object, Object>> saveShopData(ShopDataDto.Request request) {

        ShopData shopData = shopDataRepository.findById(request.getSid())
                .orElseThrow(
                        NullPointerException::new
                );

        log.info(shopData.getSurl());

        Integer clickAgeA = 0;
        Integer clickAgeB = 0;
        Integer clickMen = 0;
        Integer clickWomen = 0;

        if (request.getGender() == 'M') {
            clickMen++;
        } else if (request.getGender() == 'W') {
            clickWomen++;
        }

        if (request.getAge() <= 29) {
            clickAgeA++;
        } else if (request.getAge() >= 30) {
            clickAgeB++;
        }

        shopDataRepository.save(
                ShopData.builder()
                        .sid(request.getSid())
                        .surl(shopData.getSurl())
                        .shop(shopData.getShop())
                        .clickAgeA(shopData.getClickAgeA() + clickAgeA)
                        .clickAgeB(shopData.getClickAgeB() + clickAgeB)
                        .clickMen(shopData.getClickMen() + clickMen)
                        .clickWomen(shopData.getClickWomen() + clickWomen)
                        .clickAll(shopData.getClickAll() + 1)
                        .simg(shopData.getSimg())
                        .build()
        );

        HashMap<Object, Object> statusAndInfo = new HashMap<>();

        statusAndInfo.put("redirect_url", shopData.getSurl());
        statusAndInfo.put("shop", shopData.getShop());
        statusAndInfo.put("status", "REDIRECT_TRUE");

        return new ResponseEntity<>(statusAndInfo, HttpStatus.OK);
    }

    public ResponseEntity<List<ShopDto.Response>> getMostViewed() {
        List<ShopDto.Response> list = shopDataRepository.findAll(Sort.by(Sort.Direction.DESC, "clickAll")).stream()
                .map(ShopDto.Response::dataResponse)
                .collect(
                        Collectors.toList()
                );
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public ResponseEntity<List<ShopDto.Response>> getAgeViewed(Integer value) {
        if (value <= 29) {
            List<ShopDto.Response> list = shopDataRepository.findAll(Sort.by(Sort.Direction.DESC, "clickAgeA")).stream()
                    .map(ShopDto.Response::dataResponse)
                    .collect(
                            Collectors.toList()
                    );
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            List<ShopDto.Response> list = shopDataRepository.findAll(Sort.by(Sort.Direction.DESC, "clickAgeB")).stream()
                    .map(ShopDto.Response::dataResponse)
                    .collect(
                            Collectors.toList()
                    );
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<ShopDto.Response>> getGenderViewed(Character value) {
        if (value == 'M') {
            List<ShopDto.Response> list = shopDataRepository.findAll(Sort.by(Sort.Direction.DESC, "clickMen")).stream()
                    .map(ShopDto.Response::dataResponse)
                    .collect(
                            Collectors.toList()
                    );
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            List<ShopDto.Response> list = shopDataRepository.findAll(Sort.by(Sort.Direction.DESC, "clickWomen")).stream()
                    .map(ShopDto.Response::dataResponse)
                    .collect(
                            Collectors.toList()
                    );
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }
}
