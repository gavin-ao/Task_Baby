package data.driven.cm.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import data.driven.cm.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

/**
 *
 */
@Component
public class RedisFactory {

    private static String REDIS_CONFIG_PROPERTIES_FILE_NAME = "application.properties";

    private static String REDIS_CONFIG_MAX_TOTAL = "config.redis.maxTotal";
    private static String REDIS_CONFIG_MAX_IDLE = "config.redis.maxIdle";
    private static String REDIS_CONFIG_TEST_ON_BORROW = "config.redis.testOnBorrow";
    private static String REDIS_CONFIG_TEST_ON_RETURN = "config.redis.testOnReturn";
    private static String REDIS_CONFIG_HOST = "config.redis.host";
    private static String REDIS_CONFIG_PORT = "config.redis.port";
    private static String REDIS_CONFIG_TIMEOUT = "config.redis.timeout";
    private static String REDIS_CONFIG_PASSWORD = "config.redis.password";


    private static JedisPool jedisPool;
    static {
        initJedisPool();
    }

    private static void initJedisPool() {
        Map<String,String> config = PropertyUtil.loadProperties(REDIS_CONFIG_PROPERTIES_FILE_NAME);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.valueOf(config.get(REDIS_CONFIG_MAX_TOTAL)));
        jedisPoolConfig.setMaxIdle(Integer.valueOf(config.get(REDIS_CONFIG_MAX_IDLE)));
        jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(config.get(REDIS_CONFIG_TEST_ON_BORROW)));
        jedisPoolConfig.setTestOnReturn(Boolean.valueOf(config.get(REDIS_CONFIG_TEST_ON_RETURN)));
        jedisPool = new JedisPool(jedisPoolConfig, config.get(REDIS_CONFIG_HOST), Integer.valueOf(config.get(REDIS_CONFIG_PORT)), Integer.valueOf(config.get(REDIS_CONFIG_TIMEOUT)), config.get(REDIS_CONFIG_PASSWORD) );
    }

    /**
     * 获取资源
     * @return
     */
    public static Jedis getResource() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
            if (jedis!=null){
                jedis.close();
            }
        }
        return jedis;
    }


    public static <T> T get(String key,Class<T> tClass){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if (jedis != null && jedis.exists(key)) {
                String valueJson = jedis.get(key);
                if(StringUtils.isNotEmpty(valueJson)){
                    return JSONObject.toJavaObject(JSON.parseObject(valueJson),tClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
    public  static String get(String key){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if (jedis != null && jedis.exists(key)) {
                String valueJson = jedis.get(key);
                return valueJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
    public  static String hget(String key,String field){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if (jedis != null && jedis.exists(key)) {
                return jedis.hget(key, field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public static <T> void set(String key,T t){
        set(key,t,Constant.REDIS_DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 当seconds为-1时永久生效
     * @param key
     * @param t
     * @param seconds
     * @param <T>
     */
    public static <T> void set(String key,T t, int seconds){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                boolean isForever = seconds == -1;
                if(isForever){
                    seconds = 10;
                }
                jedis.setex(key, seconds,JSONObject.toJSONString(t));
                //如果要设置为永久有效
                if(isForever){
                    jedis.persist(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 当seconds为-1时永久生效
     * @param key
     * @param value
     * @param milliseconds
     */
    public static void setString(String key,String value, long milliseconds){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                boolean isForever = milliseconds == -1;
                if(isForever){
                    milliseconds = 10;
                }
                jedis.psetex(key, milliseconds, value);
                //如果要设置为永久有效
                if(isForever){
                    jedis.persist(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public static Long hset(String key, String field, String value){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                return jedis.hset(key, field,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 设置key值的失效时间  秒
     * @param key
     * @param seconds
     * @return
     */
    public static Long expire(String key, int seconds){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                return jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
    /**
     * 设置key值的失效时间  毫秒
     * @param key
     * @param milliseconds
     * @return
     */
    public static Long pexpire(String key, long milliseconds){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                return jedis.pexpire(key, milliseconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public static void remove(String key){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                jedis.del(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public static void hdel(String key, String... fields){
        Jedis jedis = null;
        try {
            jedis = getResource();
            if(jedis == null){
                jedis = getResource();
            }
            if(jedis != null){
                jedis.hdel(key, fields);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

}
