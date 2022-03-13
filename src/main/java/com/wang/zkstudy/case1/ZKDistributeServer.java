package com.wang.zkstudy.case1;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @Aythor lucksoul 王吉慧
 * @date 2022/3/13 10:29
 * @description
 */
public class ZKDistributeServer {

    private String connectString ="localhost:2181,localhost:2182,localhost:2183";
    private int sessionTimeout =3000;
    private ZooKeeper zooKeeper;

    @Before
    public void init() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("watchedEvent:"+new Gson().toJson(watchedEvent));


            }
        });
    }

    public void register(String hostname) throws KeeperException, InterruptedException {
        zooKeeper.create("/study/servers/" + hostname,hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online");
    }

    public void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }


    @Test
    public void addone() throws KeeperException, InterruptedException {
        register("localhost-1");
        business();
    }

    @Test
    public void addtwo() throws KeeperException, InterruptedException {
        register("localhost-2");
        business();
    }

    @Test
    public void addthree() throws KeeperException, InterruptedException {
        register("localhost-3");
        business();
    }
}
