package school.bonobono.fyb.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.bonobono.fyb.global.redis.RedisDao;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisDao redisDao;

    public List<Long> getSortedShopId(String pattern) {
        // Redis의 해당 패턴의 key 를 전부 가져옴
        Set<String> keys = redisDao.getKeys("*" + pattern);

        // 해당 패턴의 value 값에 따라 정렬
        List<Long> sortedShopIdList = keys.stream().sorted((a, b) -> {
            String viewsA = redisDao.getValues(a);
            String viewsB = redisDao.getValues(b);
            return Integer.parseInt(viewsB) - Integer.parseInt(viewsA);
        }).map(key -> Long.parseLong(key.substring(0, key.indexOf(pattern)))).toList();

        return sortedShopIdList;
    }

    public boolean saveShopData(Long id, String keyName) {
        int incrementedCount = Integer.parseInt(redisDao.getValues(id + keyName));
        redisDao.setValues(id + keyName, String.valueOf(++incrementedCount));
        return true;
    }

    public void saveInitialShopData(Long shopId) {
        redisDao.setValues(shopId + "_viewCount", "0");
        redisDao.setValues(shopId + "_maleUserCount", "0");
        redisDao.setValues(shopId + "_femaleUserCount", "0");
        redisDao.setValues(shopId + "_twenties", "0");
        redisDao.setValues(shopId + "_thirties", "0");
    }
}
