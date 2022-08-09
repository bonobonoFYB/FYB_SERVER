package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Dto.ShopDto;
import school.bonobono.fyb.Dto.UserReadDto;
import school.bonobono.fyb.Entity.Shop;
import school.bonobono.fyb.Repository.ShopRepository;
import school.bonobono.fyb.Repository.UserRepository;
import school.bonobono.fyb.Util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    // Main 홈 조회 페이지
    @Transactional
    public List<Object> getAllShopAndUserInfo() {
        List<Object> list = new ArrayList<>();
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
        list.add(shopRepository
                .findAll()
                .stream()
                .map(ShopDto.Response::response)
                .collect(
                        Collectors.toList()
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
        log.info("===================");
        log.info(request.getShop());
        log.info("===================");
        return shopRepository.findByShopContaining(request.getShop());
    }
}
