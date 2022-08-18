package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.ShopDto;
import school.bonobono.fyb.Dto.UserReadDto;
import school.bonobono.fyb.Entity.Shop;
import school.bonobono.fyb.Exception.CustomErrorCode;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Model.StatusFalse;
import school.bonobono.fyb.Repository.ShopRepository;
import school.bonobono.fyb.Repository.TokenRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
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
        if (shopList.isEmpty()){
            throw new CustomException(SEARCH_EMPTY);
        }
        return shopList;
    }

}
