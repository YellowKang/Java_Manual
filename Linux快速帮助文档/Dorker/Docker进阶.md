# 删除重复none镜像

​		将所有的none镜像删除

```
docker images -a|grep none|awk '{print $3 }'|xargs docker rmi	
```

# 批量停止删除容器

​		批量停止rancher容器

```sh
docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
```

​		批量删除rancher容器

```sh
docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh
```

​		批量删除rancher镜像

```sh
docker images| grep rancher | grep -v grep| awk '{print "docker rmi "$3}'|sh
```

# 设置容器自启动

​		启动中的容器修改重启方式

```sh
 docker update --restart=always 容器id或者容器名称
```

​		或者启动时加上参数

```sh
docker run -d \
--name test \
--restart=always \
```

# 将镜像打包压缩文件

打包过程可能会比较慢等待执行完毕即可

```sh
docker save -o  ****.tar  镜像名：镜像版本
```

将打包好的tar包加载到另一个docker中，即可把压缩包导入docker

```sh
docker load -i ****.tar  
```

# 设置网络模式为host

在Docker启动的时候都会给我们生成一个虚拟Ip，但是我们想直接使用本地的网络模式启动

```sh
docker run -d \
--name test \
--network host \
```

# Docker资源管理

​		首先我们查看Docker的系统资源	

```sh
docker system df
```

​		-V能查看相应的详细信息

```sh
docker system df -v
```

​		释放掉所有的未使用的Docker的volumes资源

```sh
docker system prune --volumes
```

​		释放掉所有的未使用的镜像和容器，包括暂停的容器（谨慎使用）

```sh
docker system prune -a
```

```sh
docker run -itd \
--name emergency-service \
-p 8999:8999 \
```

# 获取运行容器启动命令

​		使用runlike工具

```sh
pip install runlike
```

​		获取启动命令

```sh
runlike -p 容器ID
```

​		一键将所有运行中的容器

```sh
docker ps| awk '{print "runlike -p "$1 " >> ./docker-run.sh"}'|sh
```

