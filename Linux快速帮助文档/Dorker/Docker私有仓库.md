# 简介

​		Docker将仓库分为，本地仓库，公有仓库，私有仓库,如阿里云等加速也算公有仓库，我们可以去上面下载拉取镜像并且运行，但是在企业中我们会有非常多的镜像不能暴露在公网中，如项目镜像，自己研发的东西等等，但是我们在公司中又需要管理比较多的镜像，所以我们需要搭建自己的私有仓库。

​		此文档将搭建比较常见的私有Register仓库以及Harbor仓库。

# 安装Registry

## 离线安装

首先我们去一台有网的docker上pull下来镜像，然后打包成tar.gz最后load即可

打包镜像

```
docker pull registry
docker save -o registry.tar.gz registry
```

加载镜像

```
docker load -i registry.tar.gz
```

## 搭建Registry

直接启动容器即可，设置容器自启动，以及端口和挂载数据目录

此处直接写为registry-data，在docker的数据目录中创建。

```
docker run -d \
-v registry-data:/var/lib/registry \
-p 5000:5000 \
--restart=always \
--name registry \
registry
```

使用ip加端口/v2查看是否成功,如果出现空json串则成功

```
curl http://localhost:5000/v2/
```

## 连接Registry

首先备份配置文件

```
cp /etc/docker/daemon.json /etc/docker/daemon-back.json
```

我们修改Docker的配置文件修改为如下

```
vim /etc/docker/daemon.json
然后如下，修改ip即可
{
 "insecure-registries":["私有仓库ip:5000"]
}
退出重启docker容器
systemctl restart docker
```

一键修改

```
echo "{
 "insecure-registries":["192.168.1.11:5000"]
}" > /etc/docker/daemon.json
```

然后我们就能直接拉取镜像了

## 上传镜像到Registry

我们随便找到一个镜像将它更改名字，下面我们假设仓库ip为192.168.1.11，端口号为5000

拉取镜像，并且修改镜像名为ip+端口+镜像名

```
docker pull docker.io/nginx
docker tag docker.io/nginx 192.168.1.11:5000/nginx
```

然后我们推送到仓库中

```
docker push 192.168.1.11:5000/nginx
```

推送完成之后我们查看我们的镜像是否推送到仓库

```
curl http://192.168.1.11:5000/v2/_catalog
```

## 删除镜像

注意docker v2 的版本默认是不开启删除的我们需要先开启

查看默认配置

```
docker exec -it  registry sh -c 'cat /etc/docker/registry/config.yml'
```

开启删除(添加  delete: enabled: true)

```
# 在storage下面写入delete
docker exec -it registry sh -c "sed -i '/storage:/a\  delete:' /etc/docker/registry/config.yml"

# 在delete下面写入enabled: true
docker exec -it  registry sh -c "sed -i '/delete:/a\     enabled: true' /etc/docker/registry/config.yml"

# 查看配置文件
docker exec -it  registry sh -c 'cat /etc/docker/registry/config.yml'

#重启
docker restart registry
```

需要删除的时候我们需要先找到镜像的sha256

```
docker inspect 镜像名称 | grep 镜像名称
如下所示
docker inspect 192.168.1.11:5000/nginx | grep 192.168.1.11:5000/nginx@sha256
```

然后使用rest的方式删除镜像，注意镜像名为192.168.1.11:5000/nginx

请求路径为

```
192.168.1.11:5000/v2/nginx/manifests/
```

发送请求

```
curl -I -X DELETE 192.168.1.11:5000/v2/nginx/manifests/sha256:cccef6d6bdea671c394956e24b0d0c44cd82dbe83f543a47fdc790fadea48422
```

查看镜像仓库，查询不到版本则成功

```
curl 192.168.1.11:5000/v2/nginx/tags/list
```

然后释放空间

```
docker exec -it  registry sh -c 'registry garbage-collect /etc/docker/registry/config.yml'
```

## 添加用户认证

首先创建一个目录用来存放认证的信息

```
mkdir -p /docker/registry/auth
```

然后执行,前面的bigkang是用户名，后面的是密码

```
docker run --entrypoint htpasswd registry -Bbn  bigkang  bigkang > /docker/registry/auth/htpasswd
```

然后我们直接删除容器再启动（因为挂载了镜像目录所以重启并无影响，不过如果需要删除镜像则需要重新修改配置文件，或者直接挂在目录即可）

```
docker stop registry
docker rm registry
```

重启运行容器

```
docker run -d \
--name registry \
--restart=always \
-p 5000:5000 \
-v registry-data:/var/lib/registry \
-v /docker/registry/auth:/auth \
-e "REGISTRY_AUTH=htpasswd" \
-e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \
-e  REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
registry
```

用户登出

```
docker logout 192.168.1.11:5000
```

用户登录

```
docker login 192.168.1.11:5000 -u bigkang -p bigkang
```

## 上传push失败

```
如果一直超时并且失败那么就是selinux的原因，我们将它关闭掉就好了

setenforce 0 
getenforce  
```



# 安装Harbor

注意Harbor启动时会使用容器名称registry，所以需要保证这个容器名没有人使用

## 安装Docker-compose

如果离线，请下载后放入指定文件即可，这里安装1.25.0-rc1

```
curl -L https://github.com/docker/compose/releases/download/1.25.0-rc1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose

chmod +x /usr/local/bin/docker-compose

docker-compose -V
```

​		或者使用加速地址

```
curl -Lhttps://github.91chifun.workers.dev//https://github.com/docker/compose/releases/download/1.27.4/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose

```



## 下载HarBor

使用wget或者去github上下载harbor包

```
wget https://github.com/goharbor/harbor/releases/download/v2.0.0-rc1/harbor-offline-installer-v2.0.0-rc1.tgz
```

下载地址：https://github.com/goharbor/harbor/releases

选择相应版本即可

## 安装Harbor

解压并进入目录

```
tar -zxvf harbor-offline-installer-v2.0.0-rc1.tgz 
cd harbor
```

然后创建配置文件

```
echo "hostname: 180.76.143.34
http:
  port: 80
# 有https证书则配置，没有则注释掉，否则报错
# https:
  # port: 443
  # certificate: /data/harbor/nginx/certificate/path
  # private_key: /data/harbor/nginx/private/key/path

harbor_admin_password: bigkang

database:
  password: bigkang
  max_idle_conns: 50
  max_open_conns: 100

data_volume: /data/harbor/data

clair:
  updaters_interval: 12

trivy:
  ignore_unfixed: false
  skip_update: false
  insecure: false

jobservice:
  max_job_workers: 10

notification:
  webhook_job_max_retry: 10

chart:
  absolute_url: disabled

log:
  level: info
  local:
    rotate_count: 50
    rotate_size: 200M
    location: /data/harbor/log

_version: 2.0.0
proxy:
  http_proxy:
  https_proxy:
  no_proxy:
  components:
    - core
    - jobservice
    - clair
    - trivy
" > harbor.yml
```

创建目录

```
mkdir -p /data/harbor/data
mkdir -p /data/harbor/log
```

然后我们修改docker地址到harbor

备份docker配置

```
cp /etc/docker/daemon.json /etc/docker/daemon-back.json
```

```
echo "{
"insecure-registries":["180.76.143.34"]
}" > /etc/docker/daemon.json
```

重启Docker

```
systemctl restart docker
```

启动Harbor

```
./install.sh
```

然后访问80端口即可

初始化用户名admin，密码：bigkang

## 推送镜像

先登录

```
docker login 180.76.143.34 -u admin -p bigkang
```

注意此处的library为项目名称，harbor会默认初始化一个library项目，为公共

```
docker pull docker.io/nginx
docker tag docker.io/nginx 180.76.143.34:5000/library/nginx
```

