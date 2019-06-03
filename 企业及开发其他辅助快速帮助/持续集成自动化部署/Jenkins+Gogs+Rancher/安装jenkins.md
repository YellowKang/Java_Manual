# 下载jenkins最新镜像

```
docker pull jenkins/jenkins:lts
```

# 然后运行容器

我们这里需要挂载目录，由于在jenkins中需要安装jdk，以及maven所以我们将目录挂载到本地，将文件目录创建

```
创建挂载目录
mkdir -p /docker/jenkins/home

启动容器
docker run -itd \
-u root \
-p 8888:8080 \
-p 50000:50000 \
--name jenkins \
--privileged=true \
-v /var/run/docker.sock:/var/run/docker.sock -v /docker/jenkins/home:/var/jenkins_home \
docker.io/jenkins/jenkins:lts


如果启动失败，docker logs jenkins查看日志
如果是这个错误
	touch: cannot touch '/var/jenkins_home/copy_reference_file.log': Permission denied
	Can not write to /var/jenkins_home/copy_reference_file.log. Wrong volume permissions?
那么我们就给这个文件赋予权限
sudo chown -R 1000:1000 /docker/jenkins
```

启动好了之后我们访问8888端口，我们可以看到他需要一个初始化的密码，我们进入到容器中获取，或者直接从挂载目录获取

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

​		我们安装jdk只需要将linux的jdk包放到/docker/jenkins/home的路径下解压就可以了

​		![](img\jdk宿主机.png)

​		这样的话我们jdk就好了（因为jenkins只需要javahome，不需要环境变量，所以解压即可用）,但是为了安装maven所以也需要配置环境变量

在末尾加上环境变量

```
vim /etc/profile
----------------------
JAVA_HOME=/docker/jenkins/home/jdk1.8.0_211
export JAVA_HOME
export PATH=$PATH:$JAVA_HOME/bin

然后退出，然后刷新
source /etc/profile
使用命令查看版本，出现则成功
java -version
```

然后我们配置jenkins

找到你的系统管理，然后点击全局工具

​		![](img\全局maven.png)

找到Jdk，我们给他取个名字，然后我们把自动安装取消掉。配上自己的路径（注意，是容器内部的路径，例如

本地是：/docker/jenkins/home/jdk1.8.0_211，那么我们就需要配置成：/var/jenkins_home/jdk1.8.0_211），因为他是从容器内部进行查找的，所以我们要配置容器内部的

![](img\jdk配置.png)

这样Jdk就安装好了



# 安装Maven

​		我们需要在宿主机安装maven,进入jenkins挂载目录

​		cd /docker/jenkins/home

​		下载maven然后解压，配置maven_home

```
wget http://mirror.bit.edu.cn/apache/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.tar.gz
tar  -zxvf apache-maven-3.6.1-bin.tar.gz 
mv apache-maven-3.6.1 maven
rm -rf apache-maven-3.6.1-bin.tar.gz
```

​		配置maven环境

```
vim /etc/profile
----------------------
MAVEN_HOME=/docker/jenkins/home/maven
export MAVEN_HOME
export PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin
这里Maven需要java环境
然后退出，然后刷新
source /etc/profile
使用命令查看版本，出现则成功
mvn -v
```

配置阿里云加速maven仓库

```
vim /docker/jenkins/home/maven/conf/settings.xml

找到他的mirrors，将下面代码添加到最上面
<mirrors>
    <mirror>
      <id>alimaven</id>
      <name>aliyun maven</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
      <mirrorOf>central</mirrorOf>        
    </mirror>
  </mirrors>
```

​		安装完成后mvn命令测试下

​		然后我们来使用Jenkins配置Maven插件

​		找到你的系统管理，然后点击全局工具

​		![](img\全局maven.png)

Maven配置不用管我们找到maven安装，给他取个名字（随便取），然后我们把自动安装取消掉，我们自己配置路径，注意（这里的路径是docker内部的路径，这也是为什么我们需要挂载目录的原因）



![](img\全局maven安装.png)

然后点击应用，保存，maven安装完成，（注意：在提醒一次，在docker中目录是/var/jenkins_home，而在宿主机中目录是/home/jenkins，后面的路径和你安装的maven一样，但是前面一定要改成docker容器内部路径否则他会找不到）

# 进行持续集成

## 引入Maven插件

我们首先在Java程序中引入Maven的插件，并且设置好版本，image为私有仓库地址加端口号，版本为镜像版本，并且设置一个变量叫JAR_FILE，这样我们编写dockerfile的时候就能不用自己改地址了

```
    <properties>
        <java.version>1.8</java.version>
        <docker.image.prefix>111.67.196.127:5000</docker.image.prefix>
        <docker.image.version>v1.0</docker.image.version>
    </properties>
```

```

			<plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.3.7</version>
                <configuration>
                    <repository>${docker.image.prefix}/${project.artifactId}</repository>
                    <tag>${version}</tag>
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

```
FROM 111.67.196.127:5000/java1.8
ARG JAR_FILE
ENV JAVA_OPTS=""
ENV APP_OPTS=""
ADD ${JAR_FILE} app.jar
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





这样就把项目引入了，然后我们来添加maven执行命令

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



# 问题总汇

jenkins首先先检查maven的配置文件是否引入插件配置，然后jenkins的用户是否是以root启动，（不然无法使用docker），然后是docker的挂载文件以及私有仓库。（私有仓库在上面的笔记中有）