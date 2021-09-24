# 创建挂载文件夹

```sh
# 创建文件夹
mkdir -p /docker/maven-nexus/data
#创建权限
chmod /docker/maven-nexus/data
```

# 运行容器

​		启动容器命令

```sh
docker run -d \
--name maven-nexus \
--restart=always \
--privileged=true \
-p 18081:8081 \
-v /docker/maven-nexus/data:/nexus-data \
sonatype/nexus3
```
​		然后访问8081端口就能看到界面，默认账号为：admin，默认密码为：admin123

​		新版本可能需要查询密码

```sh
# Your admin user password is located in /nexus-data/admin.password on the server.
docker cp maven-nexus:/nexus-data/admin.password /tmp/nexus.password && cat /tmp/nexus.password
```

# 修改用户名以及密码

我们点击设置，找到Users，找到admin，然后change password



![](img\修改admin密码.png)

# 然后配置阿里云加速

我们点击

![](img\阿里云加速.png)



选中maven2的代理

![](img\maven-porxy.png)

nameid随便写，代理url为

```
http://maven.aliyun.com/nexus/content/groups/public/
```

如下

![](img\aliyun-porxy.png)

然后我们给public加上我们配置的加速

![](img\配置加速.png)

![](img\加速.png)

并且把它放到第一个

![](img\diyi.png)

然后保存，加速配置完成

# Maven配置连接私服

然后将maven的setting.xml改为如下servers修改你自己的maven的用户名以及密码，profiles是maven的地址只需要修改url即可，然后我们就能从maven私服下载jar包了

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <!-- 配置Maven仓库包存储地址 -->
  <localRepository>/platform/services/maven/repository</localRepository>

  <pluginGroups>
        <pluginGroup>com.spotify</pluginGroup>
  </pluginGroups>

  <proxies>
  </proxies>
  
  <!-- 配置账户id，用于访问Maven -->
  <servers>
        <server>
          	<!-- id名，maven中需要配置 -->
            <id>releases</id>
          	<!-- 私服用户名 -->
            <username>admin</username>
            <!-- 私服密码 -->
            <password>beluga</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>admin</username>
            <password>beluga</password>
        </server>
  </servers>
   
  <!-- 配置阿里云加速 -->
  <mirrors>
   
     <mirror>
        <id>nexus-aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Nexus aliyun</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public</url>
    </mirror>
    
  </mirrors>

  <profiles>
        <profile>
            <id>nexus</id>
            <repositories>
                <repository>
                  <id>nexus-aliyun</id>
                  <name>Nexus aliyun</name>
                  <url>http://maven.aliyun.com/nexus/content/groups/public</url>
                </repository>
                <repository>
                    <id>nexus</id>
                    <name>local private nexus</name>
										<url>http://152.136.68.184:8081/repository/maven-public/</url>  
                </repository>
                <repository>
                    <id>nexus-snapshots</id>
                    <name>local private nexus snapshots</name>
                    <url>http://152.136.68.184:8081/repository/maven-snapshots/</url>  
                </repository>
            </repositories>
        </profile>
  </profiles>
  <activeProfiles>
        <activeProfile>nexus</activeProfile>
  </activeProfiles>

```

# 将Maven项目上传私服

我们只需要再maven的pom.xml里面配置一下即可，这里的id就是我们设置里的用户名密码的id，url为私服地址

```
    <distributionManagement>
        <repository>
            <id>releases</id>
            <url>http://152.136.68.184:8081/repository/maven-public/</url>
        </repository>
        <snapshotRepository>
            <id>snapshots</id>
            <url>http://152.136.68.184:8081/repository/maven-snapshots/</url>
        </snapshotRepository>
    </distributionManagement>
```

然后我们使用idea将项目上传上去

打开maven点击蓝色小按钮，为了排除test的东西，然后点击deploy即可上传

![](img\depoly.png)

