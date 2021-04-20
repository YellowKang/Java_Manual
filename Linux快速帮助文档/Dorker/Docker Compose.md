# Docker Compose简介

​		什么是Docker-compose呢？前面我们使用 Docker 的时候，定义 Dockerfile 文件，然后使用 docker build、docker run 等命令操作容器。然而微服务架构的应用系统一般包含若干个微服务，每个微服务一般都会部署多个实例，如果每个微服务都要手动启停，那么效率之低，维护量之大可想而知，使用 Docker Compose 可以轻松、高效的管理容器，它是一个用于定义和运行多容器 Docker 的应用程序工具

 		简单的来说他可以帮助我们一键开启所有要部署的docker容器，也能一键关闭，是用来管理docker容器的

#  Docker Compose的安装

​		下面我们演示Linux下安装Docker Compose（联网环境）

我们首先直接去 Docker Compose的git地址查看下载命令

<https://github.com/docker/compose/releases> 

我们可以很清楚的看到现在的最新的是1.25.0

那么我们来安装它。我们看到下面有安装命令我们直接执行它的命令

```
curl -L https://github.com/docker/compose/releases/download/1.25.0-rc1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose


# 加速地址
curl -L https://github.91chifun.workers.dev/https://github.com//docker/compose/releases/download/1.29.0/docker-compose-Linux-x86_64 -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

 执行后我们查看版本，如果出现版本则安装成功

```
docker-compose -v
```

如果太慢可以将它从一台安装好的机器scp到另一台机器

```
scp docker-compose root@114.67.80.169:/usr/local/bin/docker-compose
```

# Docker Compose离线安装

我们可以去Github上下载相应的，Docker Compose选择相应的系统以及类型下载，如Linux 64位，选择

[docker-compose-Linux-x86_64](https://github.com/docker/compose/releases/download/1.25.5/docker-compose-Linux-x86_64)

将相应的文件下载下来然后放入服务器上，并且复制到/usr/local/bin

```
cp /root/docker-compose /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

然后查看版本即可

```
docker-compose -V
```

# Docker Compose 语法

​		Docker-Compose有3个版本分别是1 、2、3，我们采用最新的3的版本来进行讲解

​		Docker-Compose官方文档地址：[点击进入](https://docs.docker.com/compose/compose-file/)

​		一下是Docker官网对compose版本以及对应的Docker版本的稳定版的版本说明

​		我们则采用3.7的版本进行演示

​		首先我们使用Nginx容器来进行示例

## UP（启动命令）命令

​			首先我们适用一下命令查看docker-compose up到底支持哪些指令

```sh
docker-compose up -h
```

​			然后我们就能看到如下

```properties
   	-d, --detach               分离模式：在后台运行容器，打印新的容器名称。与--abort-on-container-exit不兼容。
   	
    --no-color                 产生单色输出，也就是我们的日志只使用一个颜色输出。
    
    --quiet-pull               Pull without printing progress information
    
    --no-deps                  Don't start linked services.
    --force-recreate           Recreate containers even if their configuration
                               and image haven't changed.
    --always-recreate-deps     Recreate dependent containers.
                               Incompatible with --no-recreate.
    --no-recreate              If containers already exist, don't recreate
                               them. Incompatible with --force-recreate and -V.
    --no-build                 Don't build an image, even if it's missing.
    --no-start                 Don't start the services after creating them.
    --build                    Build images before starting containers.
    --abort-on-container-exit  Stops all containers if any container was
                               stopped. Incompatible with -d.
    --attach-dependencies      Attach to dependent containers.
    -t, --timeout TIMEOUT      Use this timeout in seconds for container
                               shutdown when attached or when containers are
                               already running. (default: 10)
    -V, --renew-anon-volumes   Recreate anonymous volumes instead of retrieving
                               data from the previous containers.
    --remove-orphans           Remove containers for services not defined
                               in the Compose file.
    --exit-code-from SERVICE   Return the exit code of the selected service
                               container. Implies --abort-on-container-exit.
    --scale SERVICE=NUM        Scale SERVICE to NUM instances. Overrides the
                               `scale` setting in the Compose file if present.
```



## 创建简单容器

​		首先创建文件夹，以及Docker-Compose文件，文件可以以yaml和yml结尾

```shell
mkdir /root/docker-compose-file/
touch /root/docker-compose-file/docker-compose.yaml
```

​		然后编辑如下

```yml
echo "# 标记版本
version: \"3.7\"
# 配置服务
services:
  # 服务名
  nginx:
    # 镜像名
    image: nginx
    # 创建的容器名称
    container_name: nginx
    # 端口映射
    ports:
      - \"8080:80\"
    # 是否自启动
    restart: always" > docker-compose.yaml
```





```
# 导出警度data类型的索引
elasticdump --input=http://192.168.1.12:19200/pisearch/data/ --output=/Volumes/黄康/警度部署/镜像和数据/es/pisearchEsData.json
# 导出警度document类型的索引
elasticdump --input=http://192.168.1.12:19200/pisearch/document/ --output=/Volumes/黄康/警度部署/镜像和数据/es/pisearchEsDocment.json


elasticdump --input=http://192.168.1.12:19200/pisearch/ --output=/Volumes/黄康/警度部署/镜像和数据/es/pisearch.json

# 导入警度data类型的索引
elasticdump --output=http://192.168.1.12:19200/testsearch/data/ --input=/Volumes/黄康/警度部署/镜像和数据/es/pisearchEsData.json
# 导入警度data类型的索引
elasticdump --output=http://192.168.1.12:19200/testsearch/document/ --input=/Volumes/黄康/警度部署/镜像和数据/es/pisearchEsDocment.json
```

