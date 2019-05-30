package overun.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
	/**
	 * 从连接池中获取一个jedis连接
	 * */
	public Jedis getJedis(){
		return jedisPool.getResource();
	}
	
	/**操作完毕后，关闭连接*/
	public void closeJedis(Jedis jedis){
		if(jedis != null){
			jedis.close();
		}
	}
}
