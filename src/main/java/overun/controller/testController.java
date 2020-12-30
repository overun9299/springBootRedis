package overun.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import overun.redis.JedisTools;
import overun.redis.RedisClient;

/**
 * @ClassName: testController
 * @Description:
 * @author: ZhangPY
 * @version: V1.0
 * @date: 2019/5/30 10:53
 * @Copyright:
 */

@Controller
public class testController {

    @Autowired
    private JedisTools jedisTools;

    @Autowired
    private RedisClient redisClient;

    @RequestMapping(value = "/test")
    @ResponseBody
    public void testRedis() {

        try {
            String test = jedisTools.getString("test");
            if (StringUtils.isNotBlank(test)) {
                System.out.println(test);
                jedisTools.deleteKey("test");
            } else {
                jedisTools.putString("test","我的");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 发布消息
     */
    @RequestMapping(value = "/publishMessage")
    @ResponseBody
    public void publishMessage() {
        redisClient.publish("luck" , "luck please");
    }

    /**
     * 订阅消息
     */
    @RequestMapping(value = "/subscribeMessage")
    @ResponseBody
    public void subscribeMessage() {
        redisClient.subscribe(null,null,"luck");
    }


}
