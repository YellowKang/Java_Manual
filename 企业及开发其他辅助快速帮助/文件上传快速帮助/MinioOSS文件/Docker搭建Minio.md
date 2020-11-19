# 首先我们先下载镜像

```
docker pull minio/minio
```

# 然后启动容器

  这里我们使用的9000端口号可以自行修改，我们这里设置了MINIO_ACCESS_KEY，还有MINIO_SECRET_KEY为了我们登陆以及上传文件所使用，然后挂载了个文件夹

```
docker run -d \
--name minio \
-p 9000:9000 \
-e MINIO_ACCESS_KEY=bigkang123 \
-e MINIO_SECRET_KEY=bigkang123 \
-v /docker/minio/data:/data \
minio/minio server /data
```

然后我们登陆我们的Minio客户端，我们访问ip+端口就能看到如下，我们使用我们刚才设置的Key登陆

![](img\minio——login.png)

我们登陆之后我们再去创建bucket，

![](img\minio——create-bucket.png)

然后我们就能直接使用了



# 集群版

首先我们下载Docker-Compose

然后我们在本地新建一个文件

```json
touch docker-compose.yaml 
然后写入如下文件
------------------------------
version: '3.7'
services:
  minio1:
    image: minio/minio:RELEASE.2019-10-12T01-39-57Z
    volumes:
      - data1-1:/data1
      - data1-2:/data2
    ports:
      - "9001:9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio2:
    image: minio/minio:RELEASE.2019-10-12T01-39-57Z
    volumes:
      - data2-1:/data1
      - data2-2:/data2
    ports:
      - "9002:9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio3:
    image: minio/minio:RELEASE.2019-10-12T01-39-57Z
    volumes:
      - data3-1:/data1
      - data3-2:/data2
    ports:
      - "9003:9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio4:
    image: minio/minio:RELEASE.2019-10-12T01-39-57Z
    volumes:
      - data4-1:/data1
      - data4-2:/data2
    ports:
      - "9004:9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3
volumes:
  data1-1:
  data1-2:
  data2-1:
  data2-2:
  data3-1:
  data3-2:
  data4-1:
  data4-2:

```

然后启动

```
docker-compose start
```

