//package gov.cdc.srtedataservice.utilities;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RedisUtil {
//
//    final private RedisTemplate<String, Object> redisTemplate;
//
//    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public void save(String key, Object value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    public <T> T get(String key, Class<T> clazz) {
//        return clazz.cast(redisTemplate.opsForValue().get(key));
//    }
//
//
//    public void saveHashMap(String key, HashMap<?, ?> dataMap) {
//        redisTemplate.opsForHash().putAll(key, dataMap);
//    }
//
//
//    public void saveList(String key, List<?> dataList) {
//        redisTemplate.opsForList().rightPushAll(key, dataList.toArray());
//    }
//
//    public <T> List<T> getList(String key, Class<T> clazz) {
//        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
//    }
//
//    public HashMap<String, String> getHashMap(String key) {
//        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
//        HashMap<String, String> typedMap = new HashMap<>();
//        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
//            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
//                typedMap.put((String) entry.getKey(), (String) entry.getValue());
//            }
//        }
//        return typedMap;
//    }
//    public void setExpire(String key, long timeout, TimeUnit unit) {
//        redisTemplate.expire(key, timeout, unit);
//    }
//}
