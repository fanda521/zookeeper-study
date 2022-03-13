package com.wang.zkstudy;

import com.google.gson.Gson;

import lombok.SneakyThrows;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("watchedEvent:"+new Gson().toJson(watchedEvent));

                List<String> children = zooKeeper.getChildren("/study", true);
                for (String child : children) {
                    System.out.println(new Gson().toJson(child));
                }
            }
        });


    }

    /**
     * 创建节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String s = zooKeeper.create("/study", "studyData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }

    /**
     * 判断节点是否存在
     */
    @Test
    public void existsNode() throws KeeperException, InterruptedException {
        Stat exists = zooKeeper.exists("/study1", false);
        System.out.println("stat="+new Gson().toJson(exists));
    }

    /**
     * 获取节点数据
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void getNode() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/study", false, null);
        System.out.println(new String (data));
    }

    /**
     * 设置数据
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void setNodeData() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.setData("/study", "newData".getBytes(), 0);
        System.out.println(new Gson().toJson(stat));
    }

    /**
     * 获取子节点数据
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/study", false);
        for (String child : children) {
            System.out.println(new Gson().toJson(child));
        }
    }

    @Test
    public void setWatcher() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/study", true);
        for (String child : children) {
            System.out.println(new Gson().toJson(child));
        }

        Thread.sleep(Long.MAX_VALUE);
    }

}
