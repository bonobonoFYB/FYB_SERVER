package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.ShopDataDto;
import school.bonobono.fyb.Dto.ShopDto;
import school.bonobono.fyb.Dto.UserReadDto;
import school.bonobono.fyb.Entity.Shop;
import school.bonobono.fyb.Entity.ShopData;
import school.bonobono.fyb.Exception.CustomErrorCode;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Repository.ShopDataRepository;
import school.bonobono.fyb.Repository.ShopRepository;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static school.bonobono.fyb.Exception.CustomErrorCode.JWT_CREDENTIALS_STATUS_FALSE;
import static school.bonobono.fyb.Model.Model.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    public static final CustomErrorCode SEARCH_EMPTY = CustomErrorCode.SEARCH_EMPTY;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopDataRepository shopDataRepository;


    // Validation
    private void tokenCredEntialsValidate(HttpServletRequest request) {
        tokenRepository
                .findById(request.getHeader(AUTHORIZATION_HEADER))
                .orElseThrow(
                        () -> new CustomException(JWT_CREDENTIALS_STATUS_FALSE)
                );
    }

    // Service

    // Main 홈 조회 페이지
    @Transactional
    public List<Object> getAllShopAndUserInfo(HttpServletRequest headerRequest) {

        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        List<Object> list = shopRepository
                .findAll()
                .stream()
                .map(ShopDto.Response::response)
                .collect(
                        Collectors.toList()
                );

        list.add(UserReadDto.UserResponse.Response(
                        Objects.requireNonNull(
                                SecurityUtil.getCurrentUsername()
                                        .flatMap(
                                                userRepository
                                                        ::findOneWithAuthoritiesByEmail
                                        )
                                        .orElse(null))
                )
        );
        return list;
    }


    // Search 페이지 전체 조회
    @Transactional
    public List<Shop> getAllShopInfo() {
        return shopRepository.findAll();
    }

    // Search 페이지 검색 (문자열 포함 기반)
    @Transactional
    public List<Shop> getSearchShop(ShopDto.Request request) {
        List<Shop> shopList = shopRepository.findByShopContaining(request.getShop());
        if (shopList.isEmpty()) {
            throw new CustomException(SEARCH_EMPTY);
        }
        return shopList;
    }

    // 쇼핑몰 사용자 데이터 저장
    @Transactional
    public HashMap<Object, Object> saveShopData(ShopDataDto.Request request, HttpServletRequest headerRequest) {

        // 데이터 저장된 토큰 검증을 위한 Validation
        tokenCredEntialsValidate(headerRequest);

        ShopData shopData = shopDataRepository.findById(request.getSid())
                .orElseThrow(
                        () -> new NullPointerException()
                );

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
                        .surl(request.getSurl())
                        .clickAgeA(shopData.getClickAgeA() + clickAgeA)
                        .clickAgeB(shopData.getClickAgeB() + clickAgeB)
                        .clickMen(shopData.getClickMen() + clickMen)
                        .clickWomen(shopData.getClickWomen() + clickWomen)
                        .clickAll(shopData.getClickAll() + 1)
                        .build()
        );

        HashMap<Object, Object> statusAndInfo = new HashMap<>();

        statusAndInfo.put("status", "REDIRECT_TRUE");
        statusAndInfo.put("redirect_url", request.getSurl());

        return statusAndInfo;
    }
}
