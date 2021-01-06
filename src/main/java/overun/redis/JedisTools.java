package overun.redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import overun.utils.SerializeUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>jedis 客户端定义并实现操作</>
 * <pre>
 *   包含基本字符串的存储获取，key的删除，List ,Map数据类型的
 *   存储以及获取等
 * </pre>
 * @author admin
 * @date 2018-09-19
 * @version 1.0
 * */
@Component
public class JedisTools {
	private static final Logger logger = LoggerFactory.getLogger(JedisTools.class);
    /**注入jedis 连接池对象*/
	@Autowired
	private JedisPoolOperator jedisPoolOperator;
	
    /**
     * 根据key获取对应的值(String)
     * @param key 
     * @return String
     * */
    public String getString(String key) throws Exception{
		Jedis jedis = null;
		String value = "";
		try{
			if(StringUtils.isBlank(key)) return "";
			jedis = jedisPoolOperator.getJedis();
			value =  jedis.get(key);
		}catch(Exception e){
			logger.error("从redis获取数据【key="+key+"】出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
		return value;
	}
    /**
     * 根据模糊匹配keys获取对应的集合(Set)
     * @param key 
     * @return String
     * */
    public Set<String> getKeys(String key) throws Exception{
		Jedis jedis = null;
		Set<String> value = null;
		try{
			if(StringUtils.isBlank(key)) return null;
			jedis = jedisPoolOperator.getJedis();
			value =  jedis.keys(key);
		}catch(Exception e){
			logger.error("从redis获取数据【keys="+key+"】出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
		return value;
	}    
    /**
     * 插入一条String类型的数据
     * @param key
     * @param value 要插入的值
     * */
    public void putString(String key,String value) throws Exception{
    	putString(key,value,null);
    }
    
    /**
     * 插入一条String类型的数据
     * @param key
     * @param value 要插入的值
     * @param seconds 有效时间单位为秒 ,当seconds 为null时默认永不失效
     * */
    public void putString(String key,String value,Integer seconds) throws Exception{
    	Jedis jedis = null;
		try{
			if(StringUtils.isBlank(key)) return ;
			jedis = jedisPoolOperator.getJedis();
			if(seconds == null)
			 jedis.set(key, value); 	
			else
			 jedis.setex(key, seconds, value);
		}catch(Exception e){
			logger.error("redis存储【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    
    
    /**
     * 插入一个value为map 类型的数值，map存储结构为String
     * @param key
     * @param mapString 存储对象
     * */
    public void putMapString(String key,Map<String,String> mapString) throws Exception{
    	putMapString(key,mapString,null);
    }
    
    /**
     * 插入一个value为map 类型的数值，map存储结构为String
     * @param key
     * @param mapString 存储对象
     * @param seconds 有效时间单位为秒 ,当seconds 为null时默认永不失效
     * */
    public void putMapString(String key,Map<String,String> mapString,Integer seconds) throws Exception{
    	Jedis jedis = null;
		try{
			if(StringUtils.isBlank(key)) return ;
			jedis = jedisPoolOperator.getJedis();
			if(seconds == null)
			  jedis.hmset(key, mapString);
			else{
				jedis.hmset(key, mapString);
				jedis.expire(key, seconds);
			}
		}catch(Exception e){
			logger.error("redis存储map【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    /**
     * 根据key 获取一个map对象，map里存储的是String类型的数据
     * @param key
     * @return Map<String,String>
     * */
    public Map<String,String> getMapString(String key) throws Exception{
    	Jedis jedis = null;
		Map<String,String> mapInfo = null;
    	try{
			if(StringUtils.isBlank(key)) return null;
			jedis = jedisPoolOperator.getJedis();
			mapInfo =  jedis.hgetAll(key);
		}catch(Exception e){
			logger.error("redis获取map【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    	return mapInfo;
    }
    
    /**
     * 插入一个value为map 类型的数值，map存储结构为Object
     * @param key
     * @param mapString 存储对象
     * */
    public void putMapObject(String key,Map<String,T> mapString) throws Exception{
    	putMapObject(key,mapString,null);
    }
    
    /**
     * 插入一个value为map 类型的数值，map存储结构为Object
     * @param key
     * @param mapString 存储对象
     * @param seconds 有效时间单位为秒 ,当seconds 为null时默认永不失效
     * */
    public void putMapObject(String key, Map<String, T> mapString, Integer seconds) throws Exception{
    	Jedis jedis = null;
		try{
			if(StringUtils.isBlank(key)) return ;
			jedis = jedisPoolOperator.getJedis();
			if(seconds == null)
			  jedis.set(key.getBytes(), SerializeUtil.serialize(mapString));
			else{
			  jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(mapString));
			}
		}catch(Exception e){
			logger.error("redis存储map对象【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    
    
    /**
     * 根据key 获取一个map对象，map里存储的是String类型的数据
     * @param key
     * @return Map<String,String>
     * */
    @SuppressWarnings({ "hiding", "unchecked" })
	public <T>Map<String,T> getMapObject(String key) throws Exception{
    	Jedis jedis = null;
    	Map<String,T> mapInfo = null;
		try{
			if(StringUtils.isBlank(key)) return null;
			jedis = jedisPoolOperator.getJedis();
			byte [] mapByte = jedis.get(key.getBytes());
			if(null == mapByte) {return null;}
			mapInfo =  (Map<String, T>) SerializeUtil.unserialize(mapByte);
		}catch(Exception e){
			logger.error("redis获取map对象【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
		 return mapInfo;
    }
    
    
    
    /**
     * 存储一个list对象，list 里数据为String 类型
     * @param key 
     * @param list 存储对象
     * */
    public void putListString(String key,List<String> list) throws Exception{
    	putListString(key,list,null);
    }
    
    /**
     * 存储一个list对象，list 里数据为String 类型
     * @param key 
     * @param list 存储对象
     * @param seconds 有效时间单位为秒 ,当seconds 为null时默认永不失效
     * */
    public void putListString(String key,List<String> list,Integer seconds) throws Exception{
    	Jedis jedis = null;
		try{
			if(StringUtils.isBlank(key)) return ;
			jedis = jedisPoolOperator.getJedis();
			if(seconds == null)
			 jedis.set(key, JSONObject.toJSONString(list));
			else{
			 jedis.setex(key, seconds, JSONObject.toJSONString(list));	
			}
		}catch(Exception e){
			logger.error("redis存储list 【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    
    
    /**
     * 根据key 获取一个list对象，list 里数据为String 类型
     * @param key 
     * @param list 存储对象
     * */
    public List<String> getListString(String key) throws Exception{
    	Jedis jedis = null;
    	List<String> listInfo = null;
		try{
			if(StringUtils.isBlank(key)) return null;
			jedis = jedisPoolOperator.getJedis();
			String arrayStr = jedis.get(key);
			listInfo = JSONArray.parseArray(arrayStr, String.class);
		}catch(Exception e){
			logger.error("redis获取list 【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
		return listInfo;
    }
    
    /**
     * 存储一个list，list里数据为对象
     * @param key
     * @param list
     * */
    @SuppressWarnings("hiding")
	public <T> void putListObject(String key,List<T> list) throws Exception{
    	putListObject(key,list,null);
    }
    
    /**
     * 存储一个list，list里数据为对象
     * @param key
     * @param list
     * @param seconds 有效时间单位为秒 ,当seconds 为null时默认永不失效
     * */
    @SuppressWarnings("hiding")
	public <T> void putListObject(String key,List<T> list,Integer seconds) throws Exception{
    	Jedis jedis = null;
		try{
			if(StringUtils.isBlank(key)) return ;
			jedis = jedisPoolOperator.getJedis();
			if(seconds == null)
			 jedis.set(key.getBytes(), SerializeUtil.serialize(list));
			else{
				jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(list));
			}
		}catch(Exception e){
			logger.error("redis存储list对象 【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    
    
    /**
     * 存储一个list，list里数据为对象
     * @param key
     * @retrun List<T>
     * */
    @SuppressWarnings("hiding")
	public <T> List<T> getListObject(String key) throws Exception{
    	Jedis jedis = null;
    	List<T> listInfo = null;
		try{
			if(StringUtils.isBlank(key)) return null;
			jedis = jedisPoolOperator.getJedis();
			byte[] listByte = jedis.get(key.getBytes());
			listInfo = SerializeUtil.unserializeForList(listByte);
		}catch(Exception e){
			logger.error("redis获取list对象 【key="+key+"】数据出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
		return listInfo;
    }
    
    /**
     * 删除指定key
     * @param key
     * */
    public void deleteKey(String key) throws Exception{
    	Jedis jedis = null;
		try{
			if(StringUtils.isBlank(key)) return ;
			jedis = jedisPoolOperator.getJedis();
			jedis.del(key);
		}catch(Exception e){
			logger.error("redis删除 【key="+key+"】出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    /**
     * 批量删除指定keys
     * @param key
     * */
    public void deleteKeys(String... key) throws Exception{
    	Jedis jedis = null;
		try{
			if(key.length==0) return ;
			jedis = jedisPoolOperator.getJedis();
			jedis.del(key);
		}catch(Exception e){
			logger.error("redis删除 【keys="+key+"】出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    /**
     * 推送消息到redis 通道
     * @param channel 消息通道
     * @param message 消息体
     * */
    public void publish(String channel,String message){
    	Jedis jedis = null;
		try{
			jedis = jedisPoolOperator.getJedis();
			jedis.publish(channel, message);
		}catch(Exception e){
			logger.error("redis发布消息出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
    
    /**
     * 监听消息通道
     * */
    public void subscribe(Serializable sessionId, String nodeId, String... channels){
    	Jedis jedis = null;
		try{
			MyJedisSub myJedisSub = new MyJedisSub(sessionId, nodeId);
			jedis = jedisPoolOperator.getJedis();
			jedis.subscribe(myJedisSub, channels);
		}catch(Exception e){
			logger.error("redis监听消息出现异常:"+e.getMessage());
			throw new RuntimeException("10000",e);
		}finally{
			jedisPoolOperator.closeJedis(jedis);
		}
    }
}
