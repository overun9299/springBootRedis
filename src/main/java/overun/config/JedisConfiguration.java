package overun.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.util.*;

/**
 * @ClassName: JedisConfiguration
 * @Description:
 * @author: ZhangPY
 * @version: V1.0
 * @date: 2019/5/29 16:57
 * @Copyright:
 */
@Component
public class JedisConfiguration{

    private static Logger logger = LoggerFactory.getLogger(JedisConfiguration.class);

    /**  */
    @Value("${spring.redis.jedis.pool.max-active}")
    private String maxActive;
    /**  */
    @Value("${spring.redis.jedis.pool.max-idle}")
    private String maxIdle;
    /**  */
    @Value("${spring.redis.jedis.pool.min-idle}")
    private String minIdle;
    /**  */
    @Value("${spring.redis.jedis.pool.max-wait}")
    private String maxWait;
    /**  */
    @Value("${spring.redis.testOnBorrow}")
    private String testOnBorrow;
    /**  */
    @Value("${spring.redis.testWhileIdle}")
    private String testWhileIdle;
    /**  */
    @Value("${spring.redis.port}")
    private String ports;
    /**  */
    @Value("${spring.redis.timeout}")
    private String timeOut;
    /**  */
    @Value("${spring.redis.database}")
    private String dataBase;
    /**  */
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.password}")
    private String password;

    /** 哨兵master name **/
    @Value("${spring.redis.sentinel.master}")
    private String sentinelMaster;
    /** 哨兵节点 **/
    @Value("${spring.redis.sentinel.nodes}")
    private String sentinelNode;
    /** 哨兵密码 **/
    @Value("${spring.redis.sentinel.password}")
    private String sentinelPassword;


    /**
     * 创建jedis 连接池
     * */
    @Bean
    public JedisPool getJedisPool(){
        logger.info("初始化jedisPool......");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(maxActive));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
        jedisPoolConfig.setMinIdle(Integer.parseInt(minIdle));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(maxWait));
        jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
        jedisPoolConfig.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));
        int port = Integer.parseInt(ports);
        int timeout = Integer.parseInt(timeOut);
        int database = Integer.parseInt(dataBase);
        if (StringUtils.isBlank(password)) {
            password = null;
        }
        logger.info("jedisPool初始化完成......");
        return new JedisPool(jedisPoolConfig,host,port,timeout,password,database);
    }

    @Bean
    public JedisSentinelPool getJedisSentinelPool() {
        logger.info("初始化JedisSentinelPool......");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(maxActive));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
        jedisPoolConfig.setMinIdle(Integer.parseInt(minIdle));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(maxWait));
        jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
        jedisPoolConfig.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));
        int timeout = Integer.parseInt(timeOut);
        int database = Integer.parseInt(dataBase);

        Set<String> hostSet = new HashSet<>();
        List<String> strings = Arrays.asList(sentinelNode.split(","));
        hostSet.addAll(strings);
        if (StringUtils.isBlank(sentinelPassword)) {
            sentinelPassword = null;
        }
        return new JedisSentinelPool(sentinelMaster , hostSet , jedisPoolConfig , timeout , sentinelPassword , database);
    }

    /**
     * 创建一致性hash JedisPool
     * */
    @Bean
    public ShardedJedisPool getShardedJedisPool(){
        logger.info("初始化ShardedJedisPool......");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(maxActive));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
        jedisPoolConfig.setMinIdle(Integer.parseInt(minIdle));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(maxWait));
        int port = Integer.parseInt(ports);
        int timeout = Integer.parseInt(timeOut);
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host,port,timeout);
        List<JedisShardInfo> shardInfos = new ArrayList<>();
        shardInfos.add(jedisShardInfo);
        logger.info("jedisPoolConfig初始化完成......");
        return new ShardedJedisPool(jedisPoolConfig,shardInfos);
    }
}
