# 创建挂载文件夹

```
mkdir -p /docker/maven-nexus/data
```

# 运行容器

```
docker run -d \
--name maven-nexus \
--privileged=true \
-p 8081:8081 \
-v /docker/maven-nexus/data:/var/nexus-data \
sonatype/nexus3
```
然后访问8081端口就能看到界面，默认账号为：admin，默认密码为：admin123

# 修改用户名以及密码

我们点击设置，找到Users，找到admin，然后change password



![](img\修改admin密码.png)

