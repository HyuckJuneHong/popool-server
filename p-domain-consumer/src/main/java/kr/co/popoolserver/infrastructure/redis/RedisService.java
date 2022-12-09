package kr.co.popoolserver.infrastructure.redis;

public interface RedisService {

    //get
    String getValue(String key);

    //create
    void createData(String key, String value, long expired);

    //delete
    void deleteData(String key);

    //check
    void checkValue(String refreshToken, String redisToken);
}
