package overun.redis;

import redis.clients.jedis.JedisPubSub;

import java.io.Serializable;

public class MyJedisSub extends JedisPubSub {
    public  Serializable sessionId;
    public  String nodeId;

    public MyJedisSub(Serializable sessionId, String nodeId) {
        this.sessionId = sessionId;
        this.nodeId = nodeId;
    }

    @Override
    public void onMessage(String channel, String message) {
        //每次监听到频道channel发送的消息message后就在控制台打印
        System.out.println("频道" + channel + "发出消息:" + message);
    }
}
