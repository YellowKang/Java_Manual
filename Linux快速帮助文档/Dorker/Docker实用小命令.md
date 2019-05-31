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
docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh
```

批量删除rancher镜像

```
docker images| grep rancher | grep -v grep| awk '{print "docker rmi "$3}'|sh
```

docker run --name rancher -d -p 9999:8080 rancher/server

docker ps| grep k8s| grep -v grep| awk '{print "docker stop "$1}'|sh

docker ps -a| grep k8s| grep -v grep| awk '{print "docker rm "$1}'|sh

docker images| grep k8s | grep -v grep| awk '{print "docker rmi "$3}'|sh

设置容器自启动

```
 docker update --restart=always 容器id或者容器名称
```

