# Github地址

​		采用开源项目：https://github.com/yy1261633791/rtsp-web-converter

​		下载Jar包直接运行即可

# 打包为镜像

​		创建Dockerfile

```dockerfile
FROM cemmersb/centos-jdk8:latest
COPY converter-flv-2.0.5.RELEASE.jar /converter-flv-2.0.5.RELEASE.jar
ENV JAVA_OPTS=""
ENV APP_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF8 -Duser.timezone=GMT+08 -jar /converter-flv-2.0.5.RELEASE.jar $APP_OPTS" ]
EXPOSE 8081
```

​		构建镜像

```
docker build -t 192.168.1.12:5000/converter-flv .
```

​		启动容器

```
docker run -itd \
--name converter-flv \
--restart=always \
-p 8081:8081 \
192.168.1.12:5000/converter-flv
```