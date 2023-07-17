package com.coderbbs.bbsdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = BbsdemoApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        //创造key并且利用这个key往里面放value
        String redisKey = "test:uuu";
        redisTemplate.opsForValue().set(redisKey, 1);

        //打印这个数据里的值
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        //打印给所有值+1的结果
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        //打印给所有值-1的结果
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }


    @Test
    public void testHashes(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }


    @Test
    public void testLists(){
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets(){
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey, "aaa", "bbb", "ccc");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));//随机pop
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSets(){
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "aaa", 80);
        redisTemplate.opsForZSet().add(redisKey, "bbb", 90);
        redisTemplate.opsForZSet().add(redisKey, "ccc", 100);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "bbb"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "aaa"));//查询aaa排在哪
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "aaa"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys(){
        //redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        //redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    //多次访问同一个key
    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();

        //这样可以不用每次都指定key，但是不要在访问的中间查询值。Redis方法中的查询是无效的
        System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                //启用事务
                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                //这时候查询是不会立即得到数据的
                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
            }
        });
        System.out.println(obj);
    }


    //20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey = "test:hll:01";
        for (int i = 1; i<=100000; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        for (int i = 1; i<=100000; i++){
            int r = (int) Math.random()*100000+1;
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }

        //这里放key的意思是去重
        long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }


    //将三组数据合并，在合并后的重复数据中统计独立总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2 = "test:hhl:02";
        for (int i = 1; i<=10000; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hhl:03";
        for (int i = 5001; i<=15000; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }

        String redisKey4 = "test:hhl:04";
        for (int i = 10001; i<=20000; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }

        //合并会产生新数据
        String unionKey = "test:hhl:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);

        long size = redisTemplate.opsForHyperLogLog().size(unionKey);

        System.out.println(size);

    }


    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";
        //记录.这里传入的是一个逻辑，这里指的是第一个位置里存的是true
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

        //查某一位的结果。
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

        //统计一共有多少位置
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());//会统计byte里1的个数
            }
        });
        System.out.println(obj);
    }


    //统计三组数据的布尔值并对它们做or运算
    @Test
    public void testBitMapOperation(){
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2, 0, true);
        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3, 2, true);
        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4, 4, true);
        redisTemplate.opsForValue().setBit(redisKey4, 5, true);
        redisTemplate.opsForValue().setBit(redisKey4, 6, true);

        String redisKey = "test:bm:or";

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(),
                        redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 6));

    }

}
