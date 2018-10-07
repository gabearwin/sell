package com.imooc.service.impl;

import com.imooc.exception.SellException;
import com.imooc.service.SecKillService;
import com.imooc.utils.KeyUtil;
import com.imooc.utils.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 廖师兄
 * 2017-08-06 23:18
 */
@Service
public class SecKillServiceImpl implements SecKillService {

    private static final int TIMEOUT = 10 * 1000; //超时时间 10s

    @Autowired
    private RedisLock redisLock;

    // 国庆活动，皮蛋粥特价，限量100000份
    private static Map<String, Integer> products;
    private static Map<String, Integer> stock;
    private static Map<String, String> orders;

    static {
        // 模拟多个表，商品信息表，库存表，秒杀成功订单表
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();

        products.put("123456", 100000);
        stock.put("123456", 100000);
    }

    private String queryMap(String productId) {
        return "国庆活动，皮蛋粥特价，限量：" + products.get(productId) + "份"
                + " 还剩：" + stock.get(productId) + "份"
                + " 该商品成功下单用户数目：" + orders.size() + " 人";
    }

    /**
     * 查询目前的库存情况
     *
     * @param productId 商品id
     * @return
     */
    public String querySecKillProductInfo(String productId) {
        return this.queryMap(productId);
    }

    /**
     * 进行下单
     *
     * @param productId 商品id
     */
    public void orderProductMockDiffUser(String productId) {
        //加锁
        long time = System.currentTimeMillis() + TIMEOUT;
        if (!redisLock.lock(productId, String.valueOf(time))) {
            throw new SellException(101, "哎呀，人太多了，换个姿势再来！"); // 加锁失败
        }

        //1. 查询该商品库存，为0则活动结束
        int stockNum = stock.get(productId);
        if (stockNum == 0) {
            throw new SellException(100, "已经没有商品，秒杀活动结束！");
        } else {
            //2. 下单（模拟不同用户openid不同）
            orders.put(KeyUtil.genUniqueKey(), productId);

            //3. 减库存
            stockNum = stockNum - 1;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stock.put(productId, stockNum);
        }

        //解锁
        redisLock.unlock(productId, String.valueOf(time));
    }
}