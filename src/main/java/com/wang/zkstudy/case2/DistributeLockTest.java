package com.wang.zkstudy.case2;

import lombok.SneakyThrows;

/**
 * @version 1.0
 * @Aythor lucksoul 王吉慧
 * @date 2022/3/26 18:11
 * @description
 */
public class DistributeLockTest {
    public static void main(String[] args) {
        DistributeLock distributeLock = new DistributeLock();
        DistributeLock distributeLock2 = new DistributeLock();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                distributeLock.init();
                distributeLock.zkLock();
                System.out.println("线程1 获取到锁");
                Thread.sleep(5000);
                distributeLock.zkUnLock();
                System.out.println("线程1 释放锁");
            }
        }).start();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                distributeLock2.init();
                distributeLock2.zkLock();
                System.out.println("线程2 获取到锁");
                Thread.sleep(5000);
                distributeLock2.zkUnLock();
                System.out.println("线程2 释放锁");
            }
        }).start();

    }
}
