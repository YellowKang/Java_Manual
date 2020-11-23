# 创建挂载目录

```sh
mkdir -p /docker/mysql/conf
mkdir -p /docker/mysql/data
```

# 编写配置文件

```sh
touch /docker/mysql/conf/my.cnf
```

配置文件中添加

```sh
echo "[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8" > /docker/mysql/conf/my.cnf
```

# 启动容器

```sh
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

```sh
mkdir -p /docker/mysql8/conf
mkdir -p /docker/mysql8/data

vim /docker/mysql8/conf/my.cnf
```

```sh
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

启动容器

```sh
docker run -p 13306:3306 \
--name mysql8 \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql8/data:/var/lib/mysql \
-v /docker/mysql8/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:8.0.16
```

启动后执行sql命令

全都设置为utf8

```
SET NAMES utf8;
```







# 一键设置编码

```sql
set character_set_server = utf8;
set character_set_database = utf8;
set collation_connection = utf8_general_ci;
set collation_database = utf8_general_ci;
set collation_server = utf8_general_ci;
set character_set_client = utf8mb4;
set character_set_results = utf8mb4;
set character_set_connection = utf8mb4;
show variables like 'char%';
```





```sql
docker run -p 3306:3306 \
--name mysql \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/nacos/data:/var/lib/mysql \
-v /docker/nacos/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```



```

```

