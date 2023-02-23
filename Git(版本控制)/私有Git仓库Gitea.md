# 安装部署

## docker-compose

```sh
# 创建部署目录
mkdir -p ~/deploy/gitea && cd ~/deploy/gitea

# 创建启动文件启动MySQL
mkdir mysql-data && mkdir mysql-conf && mkdir gitea-data
# 初始化MySQL配置文件
cat > ./mysql-conf/mysql.cnf << EOF
[mysqld]
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
EOF


cat > ./docker-compose.yaml << EOF
version: "3"
networks:
  gitea:
    external: false
services:
  server:
    image: gitea/gitea:1.15.9
    container_name: gitea
    environment:
      - USER_UID=1000
      - USER_GID=1000
      - DB_TYPE=mysql
      - DB_HOST=db:3306
      - DB_NAME=gitea
      - DB_USER=gitea
      - DB_PASSWD=gitea
    restart: always
    networks:
      - gitea
    volumes:
      - ./gitea-data:/data
      - /etc/timezone:/etc/timezone:ro
      - /etc/localtime:/etc/localtime:ro
    ports:
       - "3000:3000"
       - "222:22"
    depends_on:
       - db
  db:
     image: mysql:8
     restart: always
     environment:
       - MYSQL_ROOT_PASSWORD=gitea
       - MYSQL_USER=gitea
       - MYSQL_PASSWORD=gitea
       - MYSQL_DATABASE=gitea
     networks:
       - gitea
     volumes:
       - ./mysql-data:/var/lib/mysql
       - ./mysql-conf/mysql.cnf:/etc/mysql/conf.d/mysql.cnf
EOF

# 启动部署脚本
docker-compose up -d
```

## k8s

