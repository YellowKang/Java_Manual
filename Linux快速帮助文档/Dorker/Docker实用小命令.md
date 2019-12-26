# 删除重复none镜像

将所有的none镜像删除

```
docker images|grep none|awk '{print $3 }'|xargs docker rmi
```

docker run --name nginx -d -p 8899:80  -v /data/nginx/conf/nginx.conf:/etc/nginx/nginx.conf  -v /data/nginx/logs:/var/log/nginx -d docker.io/nginx

# 批量停止删除容器

批量停止rancher容器

```
docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
```

批量删除rancher容器

```
docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh
```

批量删除rancher镜像

```
docker images| grep rancher | grep -v grep| awk '{print "docker rmi "$3}'|sh
```

docker run --name rancher -d -p 9999:8080 rancher/server

docker ps| grep k8s| grep -v grep| awk '{print "docker kill "$1}'|sh

docker ps -a| grep k8s| grep -v grep| awk '{print "docker rm "$1}'|sh

docker images| grep k8s | grep -v grep| awk '{print "docker rmi "$3}'|sh

# 设置容器自启动

```
 docker update --restart=always 容器id或者容器名称
```

或者启动时加上参数

```
docker run -d \
--name test \
--restart=always \
```

# 将镜像打包压缩文件

打包过程可能会比较慢等待执行完毕即可

```
docker save -o  ****.tar  镜像名：镜像版本
```

将打包好的tar包加载到另一个docker中，即可把压缩包导入docker

```
docker load -i ****.tar  
```

# 设置网络模式为host

在Docker启动的时候都会给我们生成一个虚拟Ip，但是我们想直接使用本地的网络模式启动

```
docker run -d \
--name test \
--network host \
```

