package com.wang.zkstudy.case2;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0
 * @Aythor lucksoul 王吉慧
 * @date 2022/3/26 17:34
 * @description  zk 实现分布式锁
 *
 */
public class DistributeLock {
    private String connectString ="localhost:2181,localhost:2182,localhost:2183";
    private int sessionTimeout =3000;
    private ZooKeeper zooKeeper;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    private String waitPath;
    private String currentMode;

    public void init() throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("watchedEvent:"+new Gson().toJson(watchedEvent));
                //建立了 则释放
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                getServersList();
                //waitCatch 需要释放
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)){
                    waitLatch.countDown();
                }
            }
        });

        //等待zk正常连接后，程序往下走
        connectLatch.await();

        // 判断节点/locks 是否存在
        Stat exists = zooKeeper.exists("/locks", false);
        //不存在则创建
        if (exists == null) {
            zooKeeper.create("/locks","locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

        }

    }

    public void getServersList() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/locks", true);
        System.out.println("children:" + children);
    }

    public  void getLock(String[] args) throws InterruptedException, IOException, KeeperException {
        //1.获取连接
        init();
        //2.枷锁
        zkLock();
        //3.解锁
        zkUnLock();
    }

    public void zkLock(){


        try {
            //创建带序号的临时节点
            currentMode = zooKeeper.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            //判断创建的节点是否是最小的序号节点，如果是则获取到锁，如果不是则监听他序号前的前一个节点
            List<String> children = zooKeeper.getChildren("/locks", false);
            //如果children 只有一个节点直接获取锁，如果不是，需要监控前一个节点
            if (children.size() == 1) {
                return;
            }else {
                Collections.sort(children);
                //seq-000000000
                String thisMode = currentMode.substring("/locks/".length());

                int index = children.indexOf(thisMode);
                //判断
                if(index == -1) {
                    System.out.println("数据异常");
                }else if (index == 0) {
                    return;
                }else {
                    //监听当前节点的前一个节点
                    waitPath = "/locks/" + children.get(index -1);
                    zooKeeper.getData(waitPath,true,null);
                    //等待前一个节点
                    waitLatch.await();
                    return ;
                }

            }

        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }


    public void zkUnLock() {
        try {
            zooKeeper.delete(currentMode,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
