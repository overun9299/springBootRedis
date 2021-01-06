package overun.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

/**
 * <p>jedis 对象获取以及释放</>
 * <pre>
 *   从连接池中获取jedis 连接
 *   jedis使用完成后，关闭jedis
 * </pre>
 * @author admin
 * @date 2018-09-19
 * @version 1.0
 * */
@Component
public class JedisPoolOperator {

	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private JedisSentinelPool jedisSentinelPool;

	/**
	 * 从连接池中获取一个jedis连接
	 * */
	public Jedis getJedis(){
		/** 单机模式 **/
//		return jedisPool.getResource();
		/** 哨兵模式 **/
		return jedisSentinelPool.getResource();
	}

	
	/**操作完毕后，关闭连接*/
	public void closeJedis(Jedis jedis){
		if(jedis != null){
			jedis.close();
		}
	}
}
