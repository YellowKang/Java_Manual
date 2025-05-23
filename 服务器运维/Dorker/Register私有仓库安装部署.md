# Docker-Compose

## 创建部署目录

```bash
mkdir -p ~/docker-registry

cd ~/docker-registry
```

## 生成自签证书

```bash
mkdir certs

# 创建 CA 密钥
openssl genrsa -out ca.key 4096

# 创建 CA 证书
openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 -out ca.crt \
  -subj "/C=CN/ST=Zhejiang/L=Hangzhou/O=MyOrg/OU=Dev/CN=MyOrg CA"

# 创建私钥
openssl genrsa -out registry.local.key 4096

# 创建证书请求
openssl req -new -key registry.local.key -out registry.local.csr \
  -subj "/C=CN/ST=Zhejiang/L=Hangzhou/O=MyOrg/OU=Dev/CN=registry.local"

# 创建 ext 文件（加 SAN）
cat > registry.ext <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = registry.local
EOF

# 签发证书
openssl x509 -req -in registry.local.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out registry.local.crt -days 365 -sha256 -extfile registry.ext

# 返回上级目录
cd ..
```

## Docker信任自签证书

```bash
# 创建目录
sudo mkdir -p /etc/docker/certs.d/registry.local:5000

# 复制证书
sudo cp certs/ca.crt /etc/docker/certs.d/registry.local:5000/ca.crt

# 重启Docker
sudo systemctl restart docker

```

### 禁用代理

```bash
# 编辑代理配置
vim /etc/systemd/system/docker.service.d/proxy.conf


# 写入如下排除代理的信息
[Service]
Environment="NO_PROXY=localhost,127.0.0.1,registry.local,192.168.2.0/16"



# 重新加载
sudo systemctl daemon-reload
# 重新启动
sudo systemctl restart docker
```

### 修改Host

```bash
# 编辑hosts
vim /etc/hosts


# 新增host映射
192.168.2.201 registry.local
```



## 编写Compose文件

```bash
cat > docker-compose.yml <<EOF
version: '3'
services:
  registry:
    image: registry:2
    container_name: docker-registry
    restart: always
    ports:
      - 5000:5000
    environment:
      REGISTRY_STORAGE: s3
      REGISTRY_STORAGE_S3_BUCKET: docker-register
      REGISTRY_STORAGE_S3_REGION: us-east-1       # 必须填
      REGISTRY_STORAGE_S3_REGIONENDPOINT: http://192.168.2.201:9000
      REGISTRY_STORAGE_S3_ACCESSKEY: G8IB8KRN79B1MD2SNLAK
      REGISTRY_STORAGE_S3_SECRETKEY: CG4HRzEE4j3pK10vKg+hTssaAclR5F3a37qzao0E
      REGISTRY_STORAGE_S3_SECURE: "false"
      REGISTRY_STORAGE_S3_V4AUTH: "false"
      REGISTRY_STORAGE_REDIRECT_DISABLE: "true"
      REGISTRY_STORAGE_DELETE_ENABLED: "true"
      REGISTRY_HTTP_TLS_CERTIFICATE: /certs/registry.local.crt
      REGISTRY_HTTP_TLS_KEY: /certs/registry.local.key
    volumes:
      - ./certs:/certs
EOF
```

## 启动服务

```bash
# 启动服务
sudo docker-compose up -d
```

### 测试上传

```bash
# 找一个镜像tag一下
docker tag ****   registry.local:5000/****

# 然后推送
docker push registry.local:5000/****
```

### 接口运维

```bash
# 查询镜像列表
curl -k https://registry.local:5000/v2/_catalog


# 查询镜像tag
curl -k https://registry.local:5000/v2/mysql/tags/list

# 删除功能
# 获取 digest
curl -I -k -H "Accept: application/vnd.docker.distribution.manifest.v2+json" \
  https://registry.local:5000/v2/mysql/manifests/v19.2.0
  
# 删除sha256
curl -k -X DELETE  https://registry.local:5000/v2/mysql/manifests/sha256:b420684f54516a682a567b26d2a5e4e63c6aaea3555017af38b8fa8fa5f043c0
```

# K8s方式

## 创建部署目录

```bash
# 创建部署目录
mkdir -p ~/k8s/docker-registry

# 进入目录
cd ~/k8s/docker-registry
```

## 生成自签证书

```bash
# 创建进入目录
mkdir certs
cd certs 

# 创建 CA 密钥
openssl genrsa -out ca.key 4096

# 创建 CA 证书
openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 -out ca.crt \
  -subj "/C=CN/ST=Zhejiang/L=Hangzhou/O=MyOrg/OU=Dev/CN=MyOrg CA"

# 创建私钥
openssl genrsa -out registry.local.key 4096

# 创建证书请求
openssl req -new -key registry.local.key -out registry.local.csr \
  -subj "/C=CN/ST=Zhejiang/L=Hangzhou/O=MyOrg/OU=Dev/CN=registry.local"

# 创建 ext 文件（加 SAN）
cat > registry.ext <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = registry.local
EOF

# 签发证书
openssl x509 -req -in registry.local.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out registry.local.crt -days 365 -sha256 -extfile registry.ext

# 返回上级目录
cd ..
```

### 创建命名空间

```bash
# 创建命名空间
kubectl create namespace registry
```

### k8s上传证书

```bash
# 创建证书信息
kubectl create secret tls registry-tls \
  --cert=/home/huangkang/k8s/docker-registry/certs/registry.local.crt \
  --key=/home/huangkang/k8s/docker-registry/certs/registry.local.key \
  -n registry
```

## 编写K8s Yaml文件

```bash
cat > registry-k8s-deployment.yaml <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: docker-registry
  namespace: registry
spec:
  replicas: 1
  selector:
    matchLabels:
      app: docker-registry
  template:
    metadata:
      labels:
        app: docker-registry
    spec:
      containers:
      - name: registry
        image: registry:2
        ports:
        - containerPort: 5000
        env:
        - name: REGISTRY_STORAGE
          value: s3
        - name: REGISTRY_STORAGE_S3_BUCKET
          value: docker-register
        - name: REGISTRY_STORAGE_S3_REGION
          value: us-east-1
        - name: REGISTRY_STORAGE_S3_REGIONENDPOINT
          value: http://192.168.2.201:9000
        - name: REGISTRY_STORAGE_S3_ACCESSKEY
          value: G8IB8KRN79B1MD2SNLAK
        - name: REGISTRY_STORAGE_S3_SECRETKEY
          value: CG4HRzEE4j3pK10vKg+hTssaAclR5F3a37qzao0E
        - name: REGISTRY_STORAGE_S3_SECURE
          value: "false"
        - name: REGISTRY_STORAGE_S3_V4AUTH
          value: "false"
        - name: REGISTRY_STORAGE_REDIRECT_DISABLE
          value: "true"
        - name: REGISTRY_STORAGE_DELETE_ENABLED
          value: "true"
        - name: REGISTRY_HTTP_TLS_CERTIFICATE
          value: /certs/tls.crt
        - name: REGISTRY_HTTP_TLS_KEY
          value: /certs/tls.key
        volumeMounts:
        - name: certs
          mountPath: /certs
          readOnly: true
      volumes:
      - name: certs
        secret:
          secretName: registry-tls
---
apiVersion: v1
kind: Service
metadata:
  name: docker-registry
  namespace: registry
spec:
  type: NodePort
  ports:
  - port: 5000
    targetPort: 5000
    nodePort: 30500
  selector:
    app: docker-registry
EOF


# 启动服务
kubectl apply -f registry-k8s-deployment.yaml
```

## 修改Hosts域名映射

```bash
sudo vim /etc/hosts


# 每台服务器下面修改为服务器自己的IP
192.168.2.200 registry.local
192.168.2.201 registry.local
192.168.2.202 registry.local
192.168.2.203 registry.local
```

## 信任自签证书 + 禁用代理

```bash
# 创建目录
sudo mkdir -p /etc/docker/certs.d/registry.local

# 从00复制证书到其他服务器
scp ~/k8s/docker-registry/certs/ca.crt 192.168.2.201:/home/huangkang/register_ca.crt
scp ~/k8s/docker-registry/certs/ca.crt 192.168.2.202:/home/huangkang/register_ca.crt
scp ~/k8s/docker-registry/certs/ca.crt 192.168.2.203:/home/huangkang/register_ca.crt


```

### docker

#### 信任证书

```bash
# 复制证书
sudo mkdir -p /etc/docker/certs.d/registry.local:30500
sudo cp /home/huangkang/register_ca.crt /etc/docker/certs.d/registry.local:30500/ca.crt

```

#### 禁用排除部分代理

```bash

# 编辑代理配置
sudo vim /etc/systemd/system/docker.service.d/proxy.conf

# 写入如下排除代理的信息
[Service]
Environment="NO_PROXY=localhost,127.0.0.1,registry.local,192.168.2.0/16"

```

#### 执行

```bash
# 重新加载
sudo systemctl daemon-reload
# 重新启动
sudo systemctl restart docker
```

#### 测试

```bash
# 查询镜像列表
curl -k https://registry.local:30500/v2/_catalog

# 拉取镜像
sudo docker pull registry.local:30500/mysql:5.7

# 搜索镜像
sudo docker images | grep registry.local
```

### crio

#### 信任证书

```bash
# 复制证书
sudo mkdir -p /etc/containers/certs.d/registry.local:30500
sudo cp /home/huangkang/register_ca.crt /etc/containers/certs.d/registry.local:30500/ca.crt

```

#### 禁用排除部分代理

```bash
# 编辑代理配置
sudo vim /etc/systemd/system/crio.service.d/http-proxy.conf

# 写入如下排除代理的信息
[Service]
Environment="NO_PROXY=localhost,127.0.0.1,192.168.2.0/16,dockerhub.kubekey.local,registry.local"

```

#### 执行

```bash
# 重新加载
sudo systemctl daemon-reload
# 重新启动
sudo systemctl restart crio
```



#### 测试

```bash
# 查询镜像列表
curl -k https://registry.local:30500/v2/_catalog

# 拉取镜像
sudo crictl pull registry.local:30500/mysql:5.7

# 搜索镜像
sudo crictl images | grep registry.local
```

