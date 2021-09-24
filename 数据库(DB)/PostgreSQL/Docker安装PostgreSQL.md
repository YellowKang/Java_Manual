# 创建挂载文件

```
mkdir -p /docker/postgresql/data
```

```
docker run -d \
--name postgresql \
-p 5432:5432 \
-e ALLOWIPRANGE=0.0.0.0/0 \
-e POSTGRES_USER=minexhb \
-e POSTGRES_PASS='minexhb' \
-v /docker/postgis/data:/var/lib/postgresql \
kartoza/postgis:9.5-2.2

apt-get update
apt install vim


vim /etc/profile
# 然后将export LANG="C.UTF-8"加到最后一行
# 然后重新加载
source /etc/profile

locale


sudo apt-get install postgresql-9.5 postgis-2.2 postgresql-client-9.5 -y

vim /etc/postgresql/9.5/main/pg_hba.conf
修改127.0.0.1 为0.0.0.0
host    all             all             0.0.0.0/0            md5

vim /etc/postgresql/9.5/main/postgresql.conf 
将listen_addresses='localhost'找到，并且修改为本地内网ip并且解开注释
然后重启启动
systemctl restart postgresql

修改密码
su postgres
设置编码
psql
然后连接
alter user postgres with password 'minexhb';


然后使用工具连接然后初始化postgis
CREATE EXTENSION postgis;

 

docker run -d --name postgresql -p 5432:5432 -e ALLOWIPRANGE=0.0.0.0/0 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres kartoza/postgis:9.5-2.2
```





```
export LANG=en_US.UTF-8
export LC_CTYPE="en_US.UTF-8"
export LC_NUMERIC=zh_CN.UTF-8
export LC_TIME=zh_CN.UTF-8
export LC_COLLATE="en_US.UTF-8"
LC_MONETARY=zh_CN.UTF-8
LC_MESSAGES="en_US.UTF-8"
LC_PAPER=zh_CN.UTF-8
LC_NAME=zh_CN.UTF-8
LC_ADDRESS=zh_CN.UTF-8
LC_TELEPHONE=zh_CN.UTF-8
LC_MEASUREMENT=zh_CN.UTF-8
LC_IDENTIFICATION=zh_CN.UTF-8
```

 docker run --name your-postgresql -v ~/Docker/your-postgresql/data:/home/data/ -e POSTGRES_PASSWORD=xxxxxx -d -p 5432:5432 postgres  



删除

```
rm -rf /docker/postgresql/data
```

