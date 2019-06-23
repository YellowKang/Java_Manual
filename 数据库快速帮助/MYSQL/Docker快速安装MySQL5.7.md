# 创建挂载目录

```
mkdir -p /docker/mysql/conf
mkdir -p /docker/mysql/data
```

# 编写配置文件

```
vim /docker/mysql/conf/my.cnf
```

```
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

# 启动容器

```
docker run -p 3306:3306 \
--name mysql \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql/data:/var/lib/mysql \
-v /docker/mysql/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```

# 安装MySQL8

和上面一样

```
mkdir -p /docker/mysql8/conf
mkdir -p /docker/mysql8/data

vim /docker/mysql8/conf/my.cnf
```

```
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

启动容器

```
docker run -p 13306:3306 \
--name mysql8 \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql8/data:/var/lib/mysql \
-v /docker/mysql8/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:8.0.16
```

