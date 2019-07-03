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
-v /docker/minio/data:/data minio/minio server /data
```

然后我们登陆我们的Minio客户端，我们访问ip+端口就能看到如下，我们使用我们刚才设置的Key登陆

![](img\minio——login.png)

我们登陆之后我们再去创建bucket，

![](img\minio——create-bucket.png)

然后我们就能直接使用了





