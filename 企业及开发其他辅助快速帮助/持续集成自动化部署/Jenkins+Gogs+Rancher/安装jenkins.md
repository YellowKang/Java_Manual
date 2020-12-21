# 下载jenkins最新镜像

```shell
docker pull jenkins/jenkins:lts
```

# Linux版本

```shell
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
yum install jenkins
```

下面是官网地址

<https://pkg.jenkins.io/redhat-stable/>

# 然后运行容器

​		我们这里需要挂载目录，由于在jenkins中需要安装jdk，以及maven所以我们将目录挂载到本地，将文件目录创建。

​		直接将宿主机中的Docker挂载到我们自己的Jenkins中

```shell
创建挂载目录
mkdir -p /docker/jenkins/home

启动容器
docker run -itd \
-u root \
-p 8888:8080 \
-p 50000:50000 \
--name jenkins \
--privileged=true \
--restart=always \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /docker/jenkins/home:/var/jenkins_home \
-v /etc/localtime:/etc/localtime:ro \
-v $(which docker):/usr/bin/docker \
-v $(which docker-compose):/usr/local/bin/docker-compose \
docker.io/jenkins/jenkins:lts


# -v $(which docker-compose):/usr/local/bin/docker-compose \可以自行安装或者不用

如果启动失败，docker logs jenkins查看日志
如果是这个错误
	touch: cannot touch '/var/jenkins_home/copy_reference_file.log': Permission denied
	Can not write to /var/jenkins_home/copy_reference_file.log. Wrong volume permissions?
那么我们就给这个文件赋予权限
sudo chown -R 1000:1000 /docker/jenkins
```

​		然后修改jenkins加速

```sh
# 修改
vim /docker/jenkins/home/hudson.model.UpdateCenter.xml

# 修改为如下
<?xml version='1.1' encoding='UTF-8'?>
<sites>
  <site>
    <id>default</id>
    <url>http://mirror.xmission.com/jenkins/updates/update-center.json</url>
</site>


# 加速地址，一下都是，选择一个即可
https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/update-center.json 
http://mirror.esuni.jp/jenkins/updates/update-center.json
http://mirror.xmission.com/jenkins/updates/update-center.json
```

​		启动好了之后我们访问8888端口，我们可以看到他需要一个初始化的密码，我们进入到容器中获取，或者直接从挂载目录获取

```
挂载目录获取
cat /docker/jenkins/home/secrets/initialAdminPassword

或者使用容器查看
docker exec -it jenkins  bash
cat /var/jenkins_home/secrets/initialAdminPassword
```

这样我们就获取到了密码，输入进去然后会进入到页面

![](img\jenkins01.png)

我们选择安装推荐插件，然后等待他安装完成，然后设置账号密码即可

# 安装maven插件

我们进入到jenkins，选择插件管理



![](img\系统管理.png)

![](img\插件管理.png)

可选插件搜索maven

![](img\maven插件.png)

找到这个插件，然后安装，完成后选择新建任务发现maven则安装完成

![](img\maven插件完成.png)

# 安装Jdk

​		解压JDK

```sh
# 解压
tar -zxvf jdk-8u211-linux-x64.tar.gz

# 复制
cp -r ./jdk1.8.0_211 /docker/jenkins/home
```

​		我们安装jdk只需要将linux的jdk包放到/docker/jenkins/home的路径下解压就可以了

​		这样的话我们jdk就好了（因为jenkins只需要javahome，不需要环境变量，所以解压即可用）,但是为了安装maven所以也需要配置环境变量

​		然后我们配置jenkins

​		找到你的系统管理，然后点击全局工具

​		![](img\全局maven.png)

找到Jdk，我们给他取个名字，然后我们把自动安装取消掉。配上自己的路径（注意，是容器内部的路径，例如

本地是：/docker/jenkins/home/jdk1.8.0_211，那么我们就需要配置成：/var/jenkins_home/jdk1.8.0_211），因为他是从容器内部进行查找的，所以我们要配置容器内部的

![](img\jdk配置.png)

这样Jdk就安装好了



# 安装Maven

​		我们需要在宿主机安装maven,进入jenkins挂载目录

​		cd /docker/jenkins/home

​		下载maven然后解压，或者手动下载后解压

```sh
wget https://apache.website-solution.net/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /docker/jenkins/home
cd /docker/jenkins/home
tar  -zxvf apache-maven-3.6.3-bin.tar.gz 
mv apache-maven-3.6.3 maven
rm -rf apache-maven-3.6.3-bin.tar.gz
```

​		配置maven环境

​		配置阿里云加速maven仓库

```sh
# 修改Setting文件
vim /docker/jenkins/home/maven/conf/settings.xml

# 修改为如下
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
          
  <!-- 配置Maven仓库包存储地址 -->
	<localRepository>/var/jenkins_home/maven/repository</localRepository>

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
  			<!-- 配置私服地址 -->
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
										<url>http://192.168.1.11:8081/repository/maven-public/</url>  
                </repository>
                <repository>
                    <id>nexus-snapshots</id>
                    <name>local private nexus snapshots</name>
                    <url>http://192.168.1.11:8081/repository/maven-snapshots/</url>  
                </repository>
            </repositories>
        </profile>
  </profiles>
  <activeProfiles>
        <activeProfile>nexus</activeProfile>
  </activeProfiles>
```

如果需要使用maven私服请修改配置

```
找到servers进行添加
        <server>
            <id>releases</id>
            <username>bigkang</username>
            <password>bigkang</password>
        </server>
        <server>
            <id>Snapshots</id>
            <username>bigkang</username>
            <password>bigkang</password>
        </server>
然后找到profiles添加私有仓库地址
        <profile>
            <id>nexus</id>
            <repositories>
                <repository>
                    <id>nexus</id>
                    <name>local private nexus</name>
                    <url>http://111.67.196.127:8081/repository/maven-public/</url>
                </repository>
            </repositories>
        </profile>
        <profile>
            <id>nexus-snapshots</id>
            <repositories>
                <repository>
                    <id>nexus-snapshots</id>
                    <name>local private nexus snapshots</name>
                    <url>http://111.67.196.127:8081/repository/maven-snapshots/</url>
                </repository>
            </repositories>
        </profile>
然后引入
    <activeProfiles>
        <activeProfile>nexus</activeProfile>
        <activeProfile>nexus-snapshots</activeProfile>
    </activeProfiles>
```

​		安装完成后mvn命令测试下

​		然后我们来使用Jenkins配置Maven插件

​		找到你的系统管理，然后点击全局工具

​		![](img\全局maven.png)

Maven配置不用管我们找到maven安装，给他取个名字（随便取），然后我们把自动安装取消掉，我们自己配置路径，注意（这里的路径是docker内部的路径，这也是为什么我们需要挂载目录的原因）

​		

```
/var/jenkins_home/maven
```

![](img\全局maven安装.png)

然后点击应用，保存，maven安装完成，（注意：在提醒一次，在docker中目录是/var/jenkins_home，而在宿主机中目录是/home/jenkins，后面的路径和你安装的maven一样，但是前面一定要改成docker容器内部路径否则他会找不到）

# 进行持续集成

## 引入Maven插件（插件方式）

我们首先在Java程序中引入Maven的插件，并且设置好版本，image为私有仓库地址加端口号，版本为镜像版本，并且设置一个变量叫JAR_FILE，这样我们编写dockerfile的时候就能不用自己改地址了

```xml
    <properties>
        <java.version>1.8</java.version>
      	<!-- 镜像仓库地址或者域名 -->
        <docker.image.prefix>111.67.196.127:5000</docker.image.prefix>
      	<!-- 镜像版本 -->
        <docker.image.version>v1.0</docker.image.version>
    </properties>
```

```xml
					<plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.3.7</version>
               <configuration>
                    <!--   docker私有仓库用户名,已登录则不需要 -->
                    <username>admin</username>
                    <password>bigkang</password>
                    <repository>${docker.image.prefix}/topcom-basis/${project.artifactId}</repository>
                    <tag>${docker.image.version}</tag>
                    <buildArgs>
                        <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
                    </buildArgs>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
```

## 编写Dockerfile

我们在当前的项目文件中加入Dockerfile，FROM指定父镜像，因为需要jdk，然后引入参数JAR_FILE也就是maven配置的地方，我们编写jdk的环境变量，方便修改参数，再编写项目变量，用于指定项目配置，我们将JAR添加到容器内部，并且设置启动命令，，最后暴露端口

注：请先安装私有仓库，并上传一个jdk1.8的版本

```
docker pull gmaslowski/jdk
docker tag gmaslowski/jdk ip:5000/java1.8
```



```
FROM 111.67.196.127:5000/java1.8
ARG JAR_FILE
ENV JAVA_OPTS=""
ENV APP_OPTS=""
ADD ${JAR_FILE} /app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF8 -Duser.timezone=GMT+08 -jar /app.jar $APP_OPTS" ]
EXPOSE 8080
```

## 修改Jenkins的Maven

由于我们用到了docker的插件，我们直接下载是会出问题了，我们需要在maven的配置文件中引入

```
vim /docker/jenkins/home/maven/conf/settings.xml
进入到我们挂载的jenkins的maven配置文件中
添加上，（注意maven也有这个标签添加时请添加到原来标签内，或者删除原标签）
<pluginGroups>
    <pluginGroup>com.spotify</pluginGroup>
</pluginGroups>

```

## 使用Jenkins持续集成

首先我们新建任务

![](img\新建任务.png)

选择maven项目，然后我们新建任务为test-spider（这里我是这样取名字，其他请根据项目名称），下滑找到源码管理选中git，我们输入项目的git的url地址，并且配置用户，用户名密码或者ssh，

![](img\git地址.png)





### 两种方式

#### 使用maven插件(推荐使用)

​		直接使用Maven命令即可推送，简单方便

​		这样就把项目引入了，然后我们来添加maven执行命令

![](img\build脚本.png)

```
clean package dockerfile:build dockerfile:push
```

首先clean清空，然后package打包，然后构建镜像，最后push到私有仓库

这些都做好了我们就来点击构建吧

![](img\构建.png)

然后我们来看下执行日志吧

![](img\构建记录.png)

我们可以看到他已经构建并且上传了，然后我们来启动容器

```
docker run ..........
```

然后我们打开rancher，就能看到新的容器再运行，我们将它克隆，然后修改容器名字，然后删除掉标签就能进行后续的自动化部署了

#### 使用docker插件（已经修改，直接使用脚本命令即可）

​	安装docker插件

![](img\docker插件.png)



我们设置地址，首先创建构建镜像，然后配置镜像版本，然后push到私有仓库，配置镜像名以及tag版本

# 问题总汇

jenkins首先先检查maven的配置文件是否引入插件配置，然后jenkins的用户是否是以root启动，（不然无法使用docker），然后是docker的挂载文件以及私有仓库。（私有仓库在上面的笔记中有）

# 微服务聚合项目打包

​		构建时指定模块project打包

```sh
 clean package -pl ./test/test-mp-security
```

​		构建Docker镜像时指定工作目录下打包的路径即可



# 常见帮助

## none镜像

再构建的过程中会有很多的none的镜像，这里推荐运行的时候的容器名称为 ：项目名-月份-日期-今日第几次构建

例如：test-spider项目在6月3号中第一次构建

docker run --name test-spider-6-3-1.。。。。。。。。

我们启动后使用rancher进行自动化部署时按照这个格式，然后我们来编写shell脚本批量删除过期停止的镜像,这里我们采用linux的定时任务执行

```
查看定时任务
crontab -l
创建shell脚本
vim /docker/rm-none-image-crontab.sh
写入如下内容
#!/bin/bash
source /etc/profile
docker images| grep none | grep -v grep| awk '{print "docker rmi "$3}'|sh
```

然后我们退出编辑然后来编写定时任务

```
crontab -e
编辑定时任务，每10分钟执行一次
SHELL=/bin/bash
*/10 * * * * /bin/bash /docker/rm-none-image-crontab.sh
```

然后重启定时任务

```
service crond restart
```

# jenkins配置ssh问题

由于使用jenkins用户进行操作所以需要创建jenkins用户

```
useradd jenkins
passwd jenkins    设置密码
su jenkins 进入jenkins用户
我们设置为jks12345678

切换root设置docker命令权限
chmod 777 /var/run/docker.sock
然后查看权限
ll /var/run/docker.sock
然后进入jenkins用户发现是否能使用docker命令
su jenkins 
docker ps
发现可以使用，然后我们生成jenkins主机的公钥
ssh-keygen -t rsa
查看公钥
cat /home/jenkins/.ssh/id_rsa.pub

然后复制公钥到第二台主机的jenkins用户中
进入第二台jenkins用户
su jenkins
测试能否使用docker命令
然后设置免密登陆
vim /home/jenkins/.ssh//authorized_keys
```

