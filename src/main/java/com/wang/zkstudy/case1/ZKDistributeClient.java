package com.wang.zkstudy.case1;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @Aythor lucksoul 王吉慧
 * @date 2022/3/13 12:32
 * @description
 */
public class ZKDistributeClient {
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
                getServersList();

            }
        });
    }

    public void getServersList() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/study/servers", true);
        System.out.println("children:" + children);
    }

    public void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void water() throws KeeperException, InterruptedException {
        getServersList();

        business();
    }

}
