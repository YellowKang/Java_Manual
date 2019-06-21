# 使用Docker安装



我们直接下载镜像然后运行

我们先去docker镜像仓库看他的最新版本

https://hub.docker.com/r/xuxueli/xxl-job-admin/tags    根据自身情况选择版本

```
docker pull xuxueli/xxl-job-admin:2.0.2
```

然后运行容器

```
docker run -d \
--name xxl-job \
 -e PARAMS="--spring.datasource.url=jdbc:mysql://172.21.0.16:3306/xxl_job?Unicode=true&characterEncoding=UTF-8 --spring.datasource.password=bigkang" \
-p 8087:8080 \
-v /docker/xxl-job/logs:/data/applogs \
xuxueli/xxl-job-admin
```