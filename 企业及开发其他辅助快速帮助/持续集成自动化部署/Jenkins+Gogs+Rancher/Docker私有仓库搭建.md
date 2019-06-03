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

# 删除仓库镜像

注意docker v2 的版本默认是不开启删除的我们需要先开启

```
#查看默认配置
docker exec -it  registry sh -c 'cat /etc/docker/registry/config.yml'
#开启删除(添加  delete: enabled: true)
docker exec -it  registry sh -c "sed -i '/storage:/a\  delete:' /etc/docker/registry/config.yml"
docker exec -it  registry sh -c "sed -i '/delete:/a\    enabled: true' /etc/docker/registry/config.yml"
#重启
docker restart registry
```

首先我们找到需要删除的镜像然后查看他的sha256

```
docker inspect 镜像名称或者镜像id

curl --header "Accept: application/vnd.docker.distribution.manifest.v2+json" -I -XGET 111.67.196.127:5000/v2/test-spider/manifests/0.0.1-SNAPSHOT 
```

然后查看到它的sha256，我们再去删除他

```
curl -I -X DELETE 111.67.196.127:5000/v2/test-spider/manifests/sha256:88e79e725e63669d4ffcba9463328ffa9751162b20498bf1c887d8b1a8f1f259
```

```
curl 111.67.196.127:5000/v2/test-spider/tags/list
```

最后释放空间

```
docker exec -it  registry sh -c 'registry garbage-collect /etc/docker/registry/config.yml'
```



# Docker上传push失败

```
如果一直超时并且失败那么就是selinux的原因，我们将它关闭掉就好了

setenforce 0 
getenforce  
```

