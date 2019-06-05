# 创建挂载文件

```
mkdir -p /docker/postgresql/data
```

```
docker run -d \
--name postgresql \
-p 5432:5432 \
-e ALLOWIPRANGE=0.0.0.0/0 \
-e POSTGRES_DBNAME=gis \
-v /docker/postgresql/data:/var/lib/postgresql/data  \
-e POSTGRES_PASSWORD=bigkang \
kartoza/postgis
```



 docker run --name your-postgresql -v ~/Docker/your-postgresql/data:/home/data/ -e POSTGRES_PASSWORD=xxxxxx -d -p 5432:5432 postgres  



删除

```
rm -rf /docker/postgresql/data
```

