# 使用Boot整合RedisTemplate实现分布式锁

​	我们需要获取一个redis数据如果他不存在我们则给他添加，但是只能有一个人去查询数据库并进行添加，也就是说只有一个人能拿到锁，那么拿不到锁的对象睡眠100毫秒后调用自身，此时已经添加直接走redis

​	代码示例如下：

```
    public void get() throws InterruptedException {
//        从Redis读取数据，如果没有读取到，则缓存没有添加或者失效防止缓存击穿
        Object o = redisTemplate.opsForValue().get("123");
        if(o == null){
            System.out.println("尝试获取锁对象");
            Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", 321);
            if(lock){
                String name = "黄康";
//                拿到锁，给Redis添加缓存
                redisTemplate.opsForValue().set("123",name);
                //给锁设定超时时间防止死锁
                redisTemplate.opsForValue().set("lock",name , 3L, TimeUnit.SECONDS);

            }else {
//                没有拿到锁则递归调用当前方法，此时先睡眠100毫秒防止数据未添加读取
                Thread.sleep(100);
            }
            System.out.println(lock ? "成功获取到锁" : "获取锁失败");
//            调用自生方法，此时数据已经添加到Redis中直接重新读取返回
            get();
        }else{
//            已经获取到数据直接返回查询到的数据
            System.out.println(o);
        }
    }


    @Test
    public void testGet() throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                try {
                    get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
```

