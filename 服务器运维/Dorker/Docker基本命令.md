# 环境信息

## Docker信息

```
docker info
```

然后就能看到当前安装的docker的基本信息了

## Docker版本

```
docker version
```

这样就能查看当前的docker版本，以及其他的版本信息了

# 容器生命周期

## 创建容器(create)

```
docker create  --name mynginx  nginx:latest  
```

创建一个容器，但是不启动运行他

## 执行命令(exec)

```
docker exec -it 容器id或name bash
```

这样我们就执行了这个容器的bash命令，并且分配了一个伪终端

参数为

```
-d :分离模式: 在后台运行

-i :即使没有附加也保持STDIN 打开

-t :分配一个伪终端
```

```
要执行的命令如果容器中有安装则直接执行bash如果没有则使用
/bin/bash
```

## 结束，杀死(kill)

```
docker kill -s 容器名或id
```

和linux中的kill一样结束杀死这个进程，不过docker使用的是杀死容器

## 暂停容器(pause)

```
docker pause 容器名或id
```

## 解除暂停状态(unpause)

```
docker unpause 容器名或id
```

## 重启(restart)

```
docker pause 容器名或id
```

将这个容器重新启动

## 删除(rm)

```
docker pause 容器名或id
```

## 运行(run)

```
docker run 容器名或id----可以指定很多参数
```

```
-a stdin: 指定标准输入输出内容类型，可选 STDIN/STDOUT/STDERR 三项；
-d: 后台运行容器，并返回容器ID；
-i: 以交互模式运行容器，通常与 -t 同时使用；
-p: 端口映射，格式为：主机(宿主)端口:容器端口
-t: 为容器重新分配一个伪输入终端，通常与 -i 同时使用；
--name="nginx-lb": 为容器指定一个名称；
--dns 8.8.8.8: 指定容器使用的DNS服务器，默认和宿主一致；
--dns-search example.com: 指定容器DNS搜索域名，默认和宿主一致；
-h "mars": 指定容器的hostname；
-e username="ritchie": 设置环境变量；
--env-file=[]: 从指定文件读入环境变量；
--cpuset="0-2" or --cpuset="0,1,2": 绑定容器到指定CPU运行；
-m :设置容器使用内存最大值；
--net="bridge": 指定容器的网络连接类型，支持 bridge/host/none/container: 四种类型；
--link=[]: 添加链接到另一个容器；
--expose=[]: 开放一个端口或一组端口；
```

## 启动(start)

```
docker start 容器名或id
```

将停止的容器启动

## 停止(stop)

```
docker stop 容器名或id
```

将停止的容器启动

# 镜像仓库

## login(登录)

登录仓库

```
docker login [OPTIONS] [SERVER]
```

```
-u :登陆的用户名
-p :登陆的密码

docker login -u 用户名 -p 密码
```

## logout(登出)

```
docker logout
```

## pull(下载)

```
docker pull 镜像名

可以提前使用docker search 镜像进行搜索
```

## push(推送)

```
docker push myapache:v1
推送到本地仓库
```

## search(搜索)

```
docker search 搜索镜像名
```

# 镜像管理

## build(构建)

```
使用当前目录的 Dockerfile 创建镜像，标签为 runoob/ubuntu:v1。

docker build -t runoob/ubuntu:v1 . 
使用URL github.com/creack/docker-firefox 的 Dockerfile 创建镜像。

docker build github.com/creack/docker-firefox
也可以通过 -f Dockerfile 文件的位置：

$ docker build -f /path/to/a/Dockerfile .
在 Docker 守护进程执行 Dockerfile 中的指令前，首先会对 Dockerfile 进行语法检查，有语法错误时会返回：

$ docker build -t test/myapp .
Sending build context to Docker daemon 2.048 kB
Error response from daemon: Unknown instruction: RUNCMD
```

```
--build-arg=[] :设置镜像创建时的变量；
--cpu-shares :设置 cpu 使用权重；
--cpu-period :限制 CPU CFS周期；
--cpu-quota :限制 CPU CFS配额；
--cpuset-cpus :指定使用的CPU id；
--cpuset-mems :指定使用的内存 id；
--disable-content-trust :忽略校验，默认开启；
-f :指定要使用的Dockerfile路径；
--force-rm :设置镜像过程中删除中间容器；
--isolation :使用容器隔离技术；
--label=[] :设置镜像使用的元数据；
-m :设置内存最大值；
--memory-swap :设置Swap的最大值为内存+swap，"-1"表示不限swap；
--no-cache :创建镜像的过程不使用缓存；
--pull :尝试去更新镜像的新版本；
--quiet, -q :安静模式，成功后只输出镜像 ID；
--rm :设置镜像成功后删除中间容器；
--shm-size :设置/dev/shm的大小，默认值是64M；
--ulimit :Ulimit配置。
--tag, -t: 镜像的名字及标签，通常 name:tag 或者 name 格式；可以在一次构建中为一个镜像设置多个标签。
--network: 默认 default。在构建期间设置RUN指令的网络模式
```

## images(镜像列表)

```
docker images
检索所有docker镜像
```

## import(导入镜像)

```
docker import my_ubuntu_v3.tar runoob/ubuntu:v4
```

将my_ubuntu_v3.tar文件导入docker生成镜像

## rmi(删除镜像)

```
docker rmi 镜像名或id
```

## save(导出文件)

```
docker save -o my_ubuntu_v3.tar runoob/ubuntu:v3
-o :输出到的文件。
```

## load(导入镜像)

```
docker load -i my_ubuntu_v3.tar
-i :导出的压缩文件的tar包路径
```

## tag(标记，类似复制)

```
docker tag ubuntu:15.10 runoob/ubuntu:v3
```

将ubuntu:15.10镜像标记为runoob/ubuntu:v3镜像

## commit(提交，容器生成镜像)

```
docker commit -a "runoob.com" -m "my apache" a404c6c174a2  mymysql:v1 
```

```
-a :提交的镜像作者；
-c :使用Dockerfile指令来创建镜像；
-m :提交时的说明文字；
-p :在commit时，将容器暂停。

将a404c6c174a2容器生成镜像mymysql:v1
```

# 容器运维

## export(保存容器)

```
docker export -o postgres-export.tar postgres

docker save保存的是镜像（image），docker export保存的是容器（container）；
```

## inspect(查看容器信息)

```
docker inspect mysql:5.6

-f :指定返回值的模板文件。
-s :显示总的文件大小。
--type :为指定类型返回JSON。
```

## port(查看端口)

```
查看映射端口
docker port 容器名或id
```

## ps(查看容器)

```
docker ps
查看运行中的容器

docker ps -a
查看所有容器
```

## rename(重命名)

```
docker rename nginx/nginx nginx
```

## stats(容器运行状态，情况)

```
docker stats 容器名或id
查看容器运行情况
```

## top(查看进程)

```
docker top mymysql
查看容器的进程
```

## wait(阻塞运行)

```
docker wait CONTAINER
阻塞运行容器知道停止
```

## cp(复制文件)

```
docker cp /root/nginx/ngxin.conf nginx:/etc/nginx/conf/ngxin.conf

将/root/nginx/ngxin.conf复制到容器叫做nginx的/etc/nginx/conf/ngxin.conf
```

## diff(查看文件结构更改)

```
docker diff nginx
就能查到nginx的文件结构更改哪些
```

## update(运维核心，CPU，内存等)

```
docker container update [OPTIONS] CONTAINER [CONTAINER...]

--blkio-weight
0
阻塞IO(相对权重)，介于10和1000之间，或0禁用(默认为0)
--cpu-period
0
限制CPU CFS(完全公平的调度程序)周期
--cpu-quota
0
限制CPU CFS(完全公平的调度程序)配额
--cpu-rt-period
0
限制CPU实时周期(以微秒为单位)
--cpu-rt-runtime
0
以微秒为单位限制CPU实时运行时间
--cpu-shares, -c
0
CPU份额(相对权重)
--cpuset-cpus
允许执行的CPU(0-3,0)
--cpuset-mems
允许执行的内存率(0-3,0.1)
--kernel-memory
内核内存限制
--memory, -m
内存限制
--memory-reservation
内存软限制
--memory-swap
交换限制等于内存加交换：’-1‘以启用无限制的交换
--restart
重新启动在容器退出时应用的策略
```



# 容器资源管理

## volume(文件挂载)

```
docker run -d -P --name web -v /webapp training/webapp python app.py
挂载文件
```

## network（网络管理）

```
docker network create
docker network connect
docker network ls
docker network rm
docker network disconnect
docker network inspect

创建一个新的网络
docker network create -d bridge public-network

删除所有未使用的网络
docker network prune

删除一个网络
docker network rm public-network


```

# 系统日志信息

## events(docker操作日志记录)

```
docker events --since '2019-04-17'
查看docker日志


docker events --since '3m'
查看3分钟内的日志


docker events --filter 'event=stop'
查找stop事件

docker events --filter 'image=ubuntu-1:14.04'
查找这个镜像的日志

docker events --filter 'container=7805c1d35632'
查找这个容器的日志

等等等等还有很多
```

## history(查看容器操作日志)

```
docker history 容器名称或id
```

## logs(查看容器日志)

```
docker logs 容器名称或id
查看日志
docker logs -f 容器名称或id
动态查看日志
```

