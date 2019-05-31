# 创建挂载文件

```
mkdir -p /docker/registry/data
```

# 然后运行容器

```
docker run -d \
-v /docker/registry/data:/var/lib/registry \
-p 5000:5000 \
--restart=always \
--name registry \
registry
```

使用ip加端口/v2查看是否成功,如果出现空json串则成功

```
curl http://localhost:5000/v2/
```



# 然后修改本地配置

```
vim /etc/docker/daemon.json
然后如下，修改ip即可
{
 "registry-mirrors": ["https://ldlov75k.mirror.aliyuncs.com"],
 "insecure-registries":["私有仓库ip:5000"]
}

退出重启docker容器
systemctl restart docker
```

# 上传push文件

```
我们随便找到一个镜像将它更改名字，下面我们假设仓库ip为192.168.1.177

docker tag docker.io/nginx 192.168.1.177:5000/nginx

我们将nginx修改名字，修改为ip+端口号/镜像名称
然后push,注意这里我们需要ip和docker的配置文件里的ip一致

docker push docker.io/nginx 192.168.1.177:5000/nginx
```

如果上传成功访问一下是否有

```
curl http://localhost:5000/v2/_catalog
```

如果出现nginx则成功

# Docker上传push失败

```
如果一直超时并且失败那么就是selinux的原因，我们将它关闭掉就好了

setenforce 0 
getenforce  
```

