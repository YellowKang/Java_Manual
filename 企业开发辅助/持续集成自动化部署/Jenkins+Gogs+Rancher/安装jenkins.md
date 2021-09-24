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

## Docker命令直接启动

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

## Compose文件启动

```sh
# 创建挂载文件
cd ~ && mkdir -p deploy && cd deploy && mkdir -p jenkins-server && cd jenkins-server

# 创建Compose文件
cat > ./docker-compose-jenkins-server.yml << EOF
version: '3.4'
services:
  jenkins-server:
    container_name: jenkins-server       # 指定容器的名称
    image: docker.io/jenkins/jenkins:lts        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: jenkins-server					# 主机名
    ports:
      - 8888:8080
      - 50000:50000
    privileged: true
    volumes: # 挂载目录
     - /var/run/docker.sock:/var/run/docker.sock
     - /docker/jenkins/home:/var/jenkins_home
     - /etc/localtime:/etc/localtime:ro
     - $(which docker):/usr/bin/docker
     - $(which docker-compose):/usr/local/bin/docker-compose
EOF

# 文件权限
sudo chown -R 1000:1000 ./jenkins_home

# 启动（先修改下方配置再进行启动）
docker-compose -f docker-compose-jenkins-server.yml up -d
```

## Linux使用War包安装

​		下载War包

```
wget https://get.jenkins.io/war-stable/2.277.4/jenkins.war
```

​		启动Jenkins

```
nohup java -jar jenkins.war --httpPort=9999 --prefix=/jenkins > /home/jenkins/jenkins.log 2>&1 &
```



## 配置

​		然后修改jenkins加速

```sh
# 修改Docker命令方式
vim /docker/jenkins/home/hudson.model.UpdateCenter.xml
# 修改Compose方式
vim ./jenkins_home/hudson.model.UpdateCenter.xml

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
wget https://ftp.jaist.ac.jp/pub/apache/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.zip -P /docker/jenkins/home
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



# Jenkins升级

​		下载新版本Jenkins

​		清华加速下载地址：https://mirrors.tuna.tsinghua.edu.cn/jenkins/war/

```
wget https://mirrors.tuna.tsinghua.edu.cn/jenkins/war/2.289/jenkins.war
```

​		修改war包

```sh
# 复制出容器内war包进行备份
docker cp jenkins-server:/usr/share/jenkins/jenkins.war ./jenkins.war.back

# 复制到容器内部
docker cp jenkins.war jenkins-server:/usr/share/jenkins/jenkins.war
```

​		重启

```
docker restart jenkins-server
```

# Jenkins构建项目之前构建另一个项目插件

​		下载如下插件，搜索时采用Parameterized搜索

```
Parameterized Trigger plugin
```

​		![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1619433337202.png)

​		下载后在创建项目的设置中，添加项目前置构建触发器，触发为另一个项目，这样就会先构建vosp-common再构建当前项目

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1619433382190.png)



# 使用JenkinsPipeline部署Jar项目

​		首先配置Jenkins，新建项目选择Pipeline流水线

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1622707411235.png)

​		然后选择保留最近的5个构建记录时间为28天

​		![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1622707456217.png)

​			选择SCM，然后选择Git地址，从Git中拉取代码，或者直接写入脚本文件，建议Git，选择GIT地址然后选择认证，以及分值，脚本路径为根目录下的Jenkinsfile保存即可。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1622707558139.png)

​		然后来到项目中，新建Jenkinsfile

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1622707654156.png)

​		内容如下

​		记得修改Maven打包的Jar包名称为项目名

```xml
   <build>
        <!--使用项目的artifactId作为docker打包的名称-->
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
                <configuration>
                    <source>${maven.compile.source}</source>
                    <target>${maven.compile.target}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>${maven-source-plugin.version}</version>
            </plugin>
        </plugins>
    </build>
```

​		Jenkinsfile如下

​		流程：根据Jenkins参数拉取指定分支代码  -》删除公共包，重新拉取 -》maven打包 -》jar包移动到部署路径 -》创建停止以及启动脚本 -》执行停止脚本 -》执行启动脚本 -》睡眠8秒打印日志

```sh
pipeline {
    agent any
    environment {
        // 项目名称，打包后包名为admin-api-jenkins(为Jar包名称，需要修改)
        project_name = 'admin-api'
        // 部署以及启动的目录
        deploy_dir = '/home/jenkins/deploy/admin-api'
        // java启动参数，例如JVM启动参数
        java_opts = '-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom'
        // app启动参数，例如Spring参数等等
        app_opts = '--spring.profiles.active=dev --spring.datasource.url=jdbc:oracle:thin:@//172.16.36.41:1521/ORCLPDB1.domain --spring.redis.host=172.16.36.41 --rocketmq.nameSrvAddr=172.16.36.41:9876 --log4j2.logstash.address=172.16.36.41'
        // jenkins拉取代码配置的认证ID，添加Git账户设置的id
        credentials_id = 'jenkins-bigkang'

        // git地址
        git_url = 'https://127.0.0.1/py/VOSP/Admin-API.git'

        // kill shell等脚本符号不需要修改
        kill_shell_prefix = 'ps -ef | grep'
        kill_shell_suffix = '| grep -v grep |  awk \'{print $2}\' | xargs kill -9'
        lt_symbol = '>'
        run_shell_suffix = ' 2>&1 &'
    }
    stages {
        stage('check out') {
            steps {
                //拉取代码
                checkout([$class: 'GitSCM', branches: [[name: params.branch]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.credentials_id, url: env.git_url]]])
                echo 'Checkout'
            }
        }
        stage('build') {
            steps {
                sh '''
					pwd
					rm -rf /root/.m2/repository/com/botpy/vosp/vosp-common
					mvn clean package -Dmaven.test.skip=true
				'''

            }
        }
        stage('deploy') {
            steps {

                sh '''
                    ls target
                    echo ${project_name} ${deploy_dir}
                    mkdir -p ${deploy_dir}
                    cp -f ./target/${project_name}.jar ${deploy_dir}/${project_name}-jenkins.jar


cat > ${deploy_dir}/${project_name}-jenkins-start.sh << EOF
#!/bin/bash
nohup java -jar ${deploy_dir}/${project_name}-jenkins.jar ${app_opts} ${lt_symbol} ${deploy_dir}/${project_name}-jenkins.log ${run_shell_suffix}
EOF

cat > ${deploy_dir}/${project_name}-jenkins-stop.sh << EOF
#!/bin/bash
${kill_shell_prefix} ${project_name}-jenkins ${kill_shell_suffix}
EOF


					chmod 777 ${deploy_dir}/${project_name}-jenkins-start.sh
					chmod 777 ${deploy_dir}/${project_name}-jenkins-stop.sh
				'''
                script {
                    withEnv(['JENKINS_NODE_COOKIE=background_job']) {
                        sh returnStatus: true, script: "${kill_shell_prefix} ${project_name}-jenkins ${kill_shell_suffix}"
                        sh """
					        sh ${deploy_dir}/${project_name}-jenkins-start.sh
                            sleep 8
                            tail -n 1000 ${deploy_dir}/${project_name}-jenkins.log
                        """
                    }
                }
            }
        }
    }
}
```

