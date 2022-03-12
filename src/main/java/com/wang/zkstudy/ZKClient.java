package com.wang.zkstudy;

import com.google.gson.Gson;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @version 1.0
 * @Aythor lucksoul 王吉慧
 * @date 2022/3/12 21:50
 * @description 连接基操
 */
public class ZKClient {

    private String connectString ="localhost:2181,localhost:2182,localhost:2183";
    private int sessionTimeout =3000;
    private ZooKeeper zooKeeper;

    @Before
    public void init() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(new Gson().toJson(watchedEvent));
            }
        });


    }

    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String s = zooKeeper.create("/study", "studyData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }
}
