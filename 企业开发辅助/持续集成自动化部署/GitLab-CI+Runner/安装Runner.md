# 首先创建挂载目录

```
mkdir -p /docker/gitlab-runner
```

# 然后运行容器

```
docker run -d --name gitlab-runner --restart always \
-v /docker/gitlab-runner:/etc/gitlab-runner \
-v /var/run/docker.sock:/var/run/docker.sock \
gitlab/gitlab-runner
```





```
docker exec -it gitlab-runner gitlab-runner register
```