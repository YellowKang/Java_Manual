# 连接Zookeeper

```java
        /**
         * 连接字符串，IP : 端口号 示例（192.168.1.1:2181），多个节点逗号隔开
         */
        String connectString = "124.71.9.101:2181,139.9.70.155:2181,139.9.80.252:2181";

        /**
         * 会话超时时间，连接的时候多久算超时，单位毫秒
         */
        Integer sessionTimeout = 5000;
        ZooKeeper zooKeeper = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                countDownLatch.countDown();
            }
        };
        try {
            // 连接成功后，会回调watcher监听，异步释放
            zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            log.error("Connect Zookeeper failure return Error!");
            e.printStackTrace();
        }
```

# 判断Node节点是否存在并且循环监听

```java
        // 判断这个Node节点存不存在然后观察他
        ZooKeeper finalZooKeeper = zooKeeper;
        String path = "/test";
        Stat stat = null;
        try {
            stat = zooKeeper.exists(path, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 事件类型
                    Event.EventType type = event.getType();
                    // 再次监听值后的数据值
                    byte[] watchData = null;
                    try {
                        if (type.equals(Event.EventType.NodeCreated)) {
                            log.info("节点创建");
                            finalZooKeeper.exists(path, this);
                        } else if (type.equals(Event.EventType.NodeDeleted)) {
                            log.info("节点删除");
                            finalZooKeeper.exists(path, this);
                        } else if (type.equals(Event.EventType.NodeDataChanged)) {
                            // 重新注册监听
                            watchData = finalZooKeeper.getData(path, this, null);
                            log.info("节点数据更变，修改后数据:{}", new String(watchData));
                        } else {
                            log.error("节点异常");
                        }
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (stat == null) {
            log.info("不存在");
        } else {
            log.info("存在");
        }
        try {
            // 睡眠，防止线程执行完毕后结束
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```

# 创建节点

​		添加一个节点

```java

        // Zookeeper的操作权限
        //    OPEN_ACL_UNSAFE   这是一个完全开放的权限。
        //    CREATOR_ALL_ACL   授予创建者身份验证ID的所有权限，指定用户读权限。
        //    READ_ACL_UNSAFE   授予用户读的权限。
        List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

        // 创建节点的模型类型
        //    PERSISTENT              客户端断开连接后，ZNode节点不会自动删除。
        //    PERSISTENT_SEQUENTIAL   客户端断开连接后，ZNode不会自动删除，节点名字自增序列。
        //    EPHEMERAL               客户端断开连接后，ZNode将被删除。
        //    EPHEMERAL_SEQUENTIAL    客户端断开连接后，ZNode将被删除，节点名字自增序列。
        CreateMode createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
        // 由于创建节点的时候会创建自增节点所以会返回节点名称
        String result = null;
        try {
            result = zooKeeper.create("/bigkang", "test".getBytes(), acl, createMode);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("创建节点返回结果：{}",result);
```

# 删除节点

​		简单删除节点

```java
        try {
            // 根据版本删除节点，如果版本错误则返回异常KeeperErrorCode
            zooKeeper.delete("/bigkang",0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
```

​		先查询版本再进行删除

```java
        try {
            // 查询ZNode节点是否存在，并且返回节点状态
            Stat exists = zooKeeper.exists("/bigkang", false);
            if(exists != null){
                // 根据版本删除节点，如果版本错误则返回异常KeeperErrorCode
                zooKeeper.delete("/bigkang",exists.getVersion());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
```

# 设置节点值





# 工具类

