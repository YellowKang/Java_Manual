# 创建挂载文件夹

```sh
# 创建挂载文件夹
export deployDir="/docker/maven-nexus/data"
mkdir -p $deployDir && chmod -R 777 $deployDir && cd $deployDir && cd ..
```

# 运行容器

## 命令启动

```sh
docker run -d \
--name maven-nexus \
--restart=always \
--privileged=true \
-p 18081:8081 \
-v $deployDir:/nexus-data \
sonatype/nexus3
```
​		然后访问8081端口就能看到界面，默认账号为：admin，默认密码为：admin123

​		新版本可能需要查询密码

```sh
# Your admin user password is located in /nexus-data/admin.password on the server.
docker cp maven-nexus:/nexus-data/admin.password /tmp/nexus.password && cat /tmp/nexus.password
```

## Compose启动

```sh
cat > docker-compose.yaml << EOF
version: '3.4'
services:
  nexus3:
    container_name: nexus3       # 指定容器的名称
    image: sonatype/nexus3     # 指定镜像和版本
    restart: always  # 自动重启
    hostname: nexus3
    ports:
      - 8081:8081
      - 8082:8082
      - 8083:8083
    environment:
      NEXUS_CONTEXT: nexus
    privileged: true
    volumes: 
      - $deployDir:/nexus-data
EOF
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

# Maven依赖冲突解决

​		使用IDEA工具

```
Maven Helper
```





# K8s部署

​		阿里云OSS部署

​		部署PV

```properties
export pvName="oss-nexus3"
export pvSize="1000Gi"
export pvcSize="1000Gi"
export ossBucket="k8s-storage-nexus3"
export ossUrl=""
export ossAkId=""
export ossAkSecret=""


cat > $pvName-storage.yaml << EOF 
apiVersion: v1
kind: PersistentVolume
metadata:
  name: $pvName-pv
  labels:
    app: $pvName
spec:
  capacity:
    storage: $pvSize
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: $name
  csi:
    driver: ossplugin.csi.alibabacloud.com
    volumeHandle: $pvName
    volumeAttributes:
      bucket: "$ossBucket"
      url: "$ossUrl"
      otherOpts: "-o max_stat_cache_size=0 -o allow_other"
      akId: "$ossAkId"
      akSecret: "$ossAkSecret"
      path: "/"
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: $pvName-pvc
  labels:
    app: $pvName
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: $pvcSize
  selector:
    matchLabels:
      app: $pvName
EOF
```



```properties

export dpName="nexus3"
export dpNameSpace="default"
export dpImage="sonatype/nexus3"
export pvcName="oss-nexus3-pv-pvc"
# 创建Tomcat部署的deployment
cat > $dpName-deployment.yaml << EOF 
apiVersion: v1
kind: Service
metadata:
  name: $dpName
  namespace: $dpNameSpace
spec:
  selector:
   app: $dpName
  ports:
  - name: http
    targetPort: 8081
    port: 8081
  - name: docker-http
    targetPort: 5000
    port: 5000
  - name: docker-https
    targetPort: 5001
    port: 5001
---
 
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $dpName
  namespace: $dpNameSpace
spec:
  replicas: 1
  selector:
   matchLabels:
     app: $dpName
  template:
   metadata:
     labels:
       app: $dpName
   spec:
     containers:
     - name: $dpName
       image: $dpImage
       ports:
       - name: http
         containerPort: 8081
       - name: docker-http
         containerPort: 5000
       - name: docker-https
         containerPort: 5001
       volumeMounts:
       - name: nexus3-data
         mountPath: /nexus-data
     volumes:
       - name: nexus3-data
         persistentVolumeClaim:
           claimName: $pvcName
EOF
```

