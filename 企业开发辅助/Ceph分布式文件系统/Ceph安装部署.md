## 安装

卸载原来的cephadm

```Bash
# 卸载
sudo apt remove -y cephadm

# 清除配置
sudo apt purge -y cephadm

# 检查是否还有存在的版本
apt list --installed | grep cephadm
```

检查python版本

```Bash
# 检查
python3 --version


# 大于3.9
 sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" install python3.10
```

下载指定版本Ceph

```Bash
# 设置版本

export CEPH_RELEASE=19.2.0 

# 下载
sudo curl -x http://192.168.2.64:3128 https://download.ceph.com/rpm-${CEPH_RELEASE}/el9/noarch/cephadm -o cephadm

# 执行权限
sudo chmod +x cephadm

# 查看版本
./cephadm version

# 添加源
 sudo http_proxy="http://192.168.2.64:3128" https_proxy="http://192.168.2.64:3128" ./cephadm add-repo --release squid
 
 # 更新源
 sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" update
 
 # 查看源版本
 sudo apt list cephadm
 
 # 安装
 sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" install cephadm
```

## 时间同步

```Bash
sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" install ntp

sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" install ntpdate

# 启动服务
sudo systemctl start ntp
sudo systemctl status ntp


# 编辑配置
sudo vim /etc/ntp.conf

# 新增如下
server 192.168.2.200 iburst


sudo systemctl restart ntp


# 测试
ntpdate -q 192.168.2.200
```

## 初始化集群

```Bash
# 初始化集群
sudo http_proxy="http://192.168.2.64:3128" https_proxy="http://192.168.2.64:3128" cephadm bootstrap --mon-ip 192.168.2.200
```

返回结果

```Bash
Ceph Dashboard is now available at:

             URL: https://hl-server-00:8443/
            User: admin
        Password: 1rwrtg408p

Enabling client.admin keyring and conf on hosts with "admin" label
Saving cluster configuration to /var/lib/ceph/64b93b00-9516-11ef-b2b2-03ccbcf6fa2d/config directory
You can access the Ceph CLI as following in case of multi-cluster or non-default config:

        sudo /usr/sbin/cephadm shell --fsid 64b93b00-9516-11ef-b2b2-03ccbcf6fa2d -c /etc/ceph/ceph.conf -k /etc/ceph/ceph.client.admin.keyring

Or, if you are only running a single cluster on this host:

        sudo /usr/sbin/cephadm shell

Please consider enabling telemetry to help improve Ceph:

        ceph telemetry on

For more information see:

        https://docs.ceph.com/en/latest/mgr/telemetry/

Bootstrap complete.
```

加载镜像

```Bash
sudo docker load  -i /home/huangkang/ceph19.2.0.tar.gz
```

## 配置集群

```Bash
# 查看master公钥
cat /etc/ceph/ceph.pub

# 分发到其他节点
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDgsjn3d4ajrl4uqN+xm5YH17HRUf5oV7M+dTxTyb/MDy2XhyOnE4mbCwdlhLKP9PAxdj4VHwy3mn5utHzIOyhSw0r9FMcrh8n4hpSxiMuGi2TEcXkuV5YNOjpNDBtqgaKnWDBowgmxGex/8ZfGkm2NElym4mZwPlMeSQuzZdaOH74Qi/qNlTamZ6ZOKqI2tJS3y1nLJCI0Gem0zne0jO+l36QveyY2UFrhRc375afIVRdE475GSxkVJYJM5KGMjIw+zdytcSxMRomKOzM2witxtI+4OxAt9yYItTB4OOZqIi1npiFUoqAoGy0bgkdbUGt3Fd7g7nIKybXOvRktOs/PmaWfV4MJAJFN3HD5cjy1JWGKxRVq15DWZkgbmaqr7ejQWmJhAzYDwb3/8YxY5nHu+UPLEYLhoVNbf3wBfrzfrWwAGCXPaN942M0pCU/PyoTPsqhSNijzm18w5x6gHtjeEOui0SC+Qhdyr3O04Pi4wvb+S7pV4vtS4zF7/SnjKVs= ceph-64b93b00-9516-11ef-b2b2-03ccbcf6fa2d" >> /root/.ssh/authorized_keys
```

新增节点

```Bash
sudo cephadm shell -- ceph orch host add hl-server-01 192.168.2.201
sudo cephadm shell -- ceph orch host add hlserver02 192.168.2.202
sudo cephadm shell -- ceph orch host add hl-server-03 192.168.2.203

# 验证
sudo cephadm shell -- ceph orch host ls
```

## 新增Monitor

```Bash
# 新建配置文件
sudo echo "service_type: mon
service_id: mon
placement:
  hosts:
    - hl-server-00
    - hl-server-01
    - hlserver02" > /home/huangkang/ceph-monitor.yaml


chmod 644 /home/huangkang/ceph-monitor.yaml

# 应用
cat /home/huangkang/ceph-monitor.yaml | sudo cephadm shell -- ceph orch apply -i -


# 查看是否安装成功
sudo cephadm shell -- ceph orch ps --daemon_type mon
```

## 新增配置OSD

```Bash
# 查看磁盘信息，以及可用磁盘
sudo fdisk -l
```

### 挂载Server00

```Bash
# 找到没有使用并且格式化后的盘，取消挂载
sudo umount /dev/nvme1n1

# 使用 wipefs 命令可以清除设备上的文件系统标记
sudo wipefs -a /dev/nvme1n1

# 使用命令挂载磁盘
sudo cephadm shell -- ceph orch daemon add osd hl-server-00:/dev/nvme1n1
```

### 挂载Server01

```Bash
# 找到没有使用并且格式化后的盘，取消挂载
sudo umount /dev/nvme2n1

# 使用 wipefs 命令可以清除设备上的文件系统标记
sudo wipefs -a /dev/nvme2n1

# 使用命令挂载磁盘
sudo cephadm shell -- ceph orch daemon add osd hl-server-01:/dev/nvme2n1



sudo umount /dev/nvme1n1

sudo wipefs -a /dev/nvme1n1

sudo cephadm shell -- ceph orch daemon add osd hl-server-01:/dev/nvme1n1
```

### 挂载Server02

```Bash
# 找到没有使用并且格式化后的盘，取消挂载
sudo umount /dev/nvme1n1

# 使用 wipefs 命令可以清除设备上的文件系统标记
sudo wipefs -a /dev/nvme1n1

# 使用命令挂载磁盘
sudo cephadm shell -- ceph orch daemon add osd hlserver02:/dev/nvme1n1





sudo umount /dev/nvme2n1

sudo wipefs -a /dev/nvme2n1

sudo cephadm shell -- ceph orch daemon add osd hlserver02:/dev/nvme2n1
```

### 挂载Server03

```Bash
# 找到没有使用并且格式化后的盘，取消挂载
sudo umount /dev/nvme2n1

# 使用 wipefs 命令可以清除设备上的文件系统标记
sudo wipefs -a /dev/nvme2n1

# 使用命令挂载磁盘
sudo cephadm shell -- ceph orch daemon add osd hl-server-03:/dev/nvme2n1

# 启动osd 如果down的话
ceph orch daemon start osd.3



# 找到没有使用并且格式化后的盘，取消挂载
sudo umount /dev/nvme1n1

sudo mkfs.ext4 /dev/nvme1n1

# 使用 wipefs 命令可以清除设备上的文件系统标记
sudo wipefs -a /dev/nvme1n1


sudo cephadm shell -- ceph orch daemon add osd hl-server-03:/dev/nvme1n1
```

### 直接挂载目录方式00

```Bash
# 00上执行
sudo mkdir -p /ceph/sys/data

sudo umount -l /dev/loop5

sudo dd if=/dev/zero of=/dev/loop5 bs=1M seek=1024000 count=1


sudo dd if=/dev/zero of=/ceph/sys/00-ceph-directory-osd.img bs=1M seek=1024000 count=0
sudo mkfs.ext4 /ceph/sys/00-ceph-directory-osd.img


# 挂载磁盘
sudo mount -o loop /ceph/sys/00-ceph-directory-osd.img /ceph/sys/data/00-ceph-directory-osd


# 查看磁盘 设备名
sudo df -h  | grep /ceph/sys/data/00-ceph-directory-osd

/dev/loop5                          984G   28K  934G   1% /ceph/sys/data/00-ceph-directory-osd


# 清空系统文件
sudo wipefs -a /dev/loop5


sudo dd if=/dev/zero of=/dev/loop5 bs=1M seek=1024000 count=0

# 新增磁盘设备到osd
sudo cephadm shell -- ceph orch daemon add osd hl-server-00:/dev/loop5


sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" install  ceph-volume
```

### 数据平衡

```Bash
# 查看osd以及权重信息
ceph osd tree

# 查看 REWEIGHT 权重
ID  CLASS  WEIGHT   TYPE NAME              STATUS  REWEIGHT  PRI-AFF
-1         8.42117  root default
-3         1.86299      host hl-server-00
 0    ssd  1.86299          osd.0              up   0.25015  1.00000
-5         2.91939      host hl-server-01
 1    ssd  1.81940          osd.1              up   0.30013  1.00000
 4    ssd  1.09999          osd.4              up   1.00000  1.00000
-9         1.81940      host hl-server-03
 3    ssd  1.81940          osd.3              up   0.25015  1.00000
-7         1.81940      host hlserver02
 2    ssd  1.81940          osd.2              up   0.25015  1.00000
 
 
 # 设置权重
 ceph osd reweight osd.1 0.30014
 
 
 # 设置自动平衡
 ceph osd reweight-by-utilization
```

### 卸载OSD

```Bash
# 清空osd信息 cephadm shell
ceph osd out osd.0
ceph osd crush remove osd.0
ceph auth del osd.0
ceph osd rm osd.0
ceph orch daemon rm osd.0 --force


ceph osd out osd.1
ceph osd crush remove osd.1
ceph auth del osd.1
ceph osd rm osd.1
ceph osd out osd.2
ceph osd crush remove osd.2
ceph auth del osd.2
ceph osd rm osd.2
ceph osd out osd.3
ceph osd crush remove osd.3
ceph auth del osd.3
ceph osd rm osd.3
ceph osd out osd.4
ceph osd crush remove osd.4
ceph auth del osd.4
ceph osd rm osd.4
ceph osd out osd.5
ceph osd crush remove osd.5
ceph auth del osd.5
ceph osd rm osd.5

# 查看逻辑卷
sudo lvdisplay
# 移除逻辑卷
sudo lvremove /dev/ceph-49ec2dab-676b-44b7-ac79-9fcb5d5ae985/osd-block-9ab2aa27-7f82-4205-b26e-51fbbb7624b7

# 查看物理卷
sudo pvdisplay
# 释放物理卷
sudo vgreduce ceph-c22cfc07-30f4-467c-8364-e31403c7055c /dev/nvme2n1

sudo pvremove --force --force /dev/nvme1n1

# 格式化 清空磁盘
sudo mkfs.ext4 /dev/nvme1n1
# 清空系统文件
sudo wipefs -a /dev/nvme1n1



# 重启osd
#ceph orch daemon stop osd.0
#ceph orch daemon start osd.0
```

## 其他运维命令

```Bash
# 查看集群情况
sudo cephadm shell -- ceph -s

# 查看monitor
sudo cephadm shell -- ceph orch ps --daemon_type mon

# 查看manager
sudo cephadm shell -- ceph orch ps --daemon_type mgr

# 查看osd
sudo cephadm shell -- ceph orch ps --daemon_type osd

# 查看节点信息
sudo cephadm shell -- ceph orch host 
# 查看模块信息
sudo cephadm shell -- ceph mgr module ls


# 查看存储池
sudo cephadm shell -- ceph osd pool ls

# 删除存储池
sudo cephadm shell -- ceph osd pool delete my_ec_pool my_ec_pool  --yes-i-really-really-mean-it


# 重启mon组件
sudo cephadm shell -- ceph orch restart mon

# 重启指定mon组件
sudo cephadm shell -- ceph orch restart mon.a

# 重启指定节点mon组件
sudo cephadm shell -- ceph orch restart mon.<节点名称>

# 重启 osd
ceph orch daemon restart osd.2
```

## 备份源目录

```Bash
/etc/apt/sources.list.d_back
```

## 纠删码

```Bash
ceph osd erasure-code-profile set my_ec_profile k=2 m=2


ceph osd pool delete my_ec_pool my_ec_pool  --yes-i-really-really-mean-it

ceph osd pool create my_ec_pool 128 128 erasure my_ec_profile
ceph osd pool application enable my_ec_pool cephfs

ceph -s
```

## OSS对象存储

### 初始化rgw配置

```Bash
ceph osd erasure-code-profile set ec_profile k=2 m=2


ceph osd pool create rgw_ec_pool 32 32 erasure ec_profile
ceph osd pool create rgw_index_pool 16 16 replicated
ceph osd pool create rgw_ec_ext_pool 16 16 replicated

ceph osd pool application enable rgw_ec_pool rgw
ceph osd pool application enable rgw_index_pool rgw
ceph osd pool application enable rgw_ec_ext_pool rgw


ceph osd pool delete rgw_ec_pool rgw_ec_pool  --yes-i-really-really-mean-it
ceph osd pool delete rgw_index_pool rgw_index_pool  --yes-i-really-really-mean-it
ceph osd pool delete rgw_ec_ext_pool rgw_ec_ext_pool  --yes-i-really-really-mean-it



radosgw-admin realm create --rgw-realm=ec-realm --default

radosgw-admin zonegroup create --rgw-zonegroup=ec-zonegroup --rgw-realm=ec-realm --master --default

radosgw-admin zone create --rgw-zonegroup=ec-zonegroup --rgw-zone=ec-zone --master --default




# 添加placement
 radosgw-admin zonegroup placement add \
    --rgw-zonegroup=ec-zonegroup \
    --placement-id=ec-zone-placement
 
radosgw-admin zone placement add \
    --rgw-zone=ec-zone \
    --placement-id=ec-zone-placement \
    --data-pool=rgw_ec_pool \
    --index-pool=rgw_index_pool \
    --data-extra-pool=rgw_ec_ext_pool



sudo ceph orch apply rgw ceph-ec-oss \
    --port 11802 \
    --placement="hl-server-00,hl-server-01" \
    --rgw-zone=ec-zone \
    --rgw-zonegroup=ec-zonegroup 


sudo ceph orch rm rgw.ceph-oss

ceph orch ps




ceph osd pool delete my_rgw_ec_pool my_rgw_ec_pool  --yes-i-really-really-mean-it

ceph osd pool delete default.rgw.buckets.index default.rgw.log  --yes-i-really-really-mean-it
ceph osd pool delete default.rgw.log default.rgw.log  --yes-i-really-really-mean-it
ceph osd pool delete default.rgw.control default.rgw.control  --yes-i-really-really-mean-it
ceph osd pool delete default.rgw.meta default.rgw.meta  --yes-i-really-really-mean-it
```

### 创建用户

```Bash
# 创建一个用户
radosgw-admin user create --uid ceph-s3-user --display-name "Ceph S3 User Demo"

# 返回如下 记录保留 access_key 和 secret_key
{
    "user_id": "ceph-s3-user",
    "display_name": "Ceph S3 User Demo",
    "email": "",
    "suspended": 0,
    "max_buckets": 1000,
    "subusers": [],
    "keys": [
        {
            "user": "ceph-s3-user",
            "access_key": "P82Y47A82XIDNKB44NFU",
            "secret_key": "Sq8yKV8wRd45xoRc6gWBW45dDFo7VMiFeD7Q8Mhc",
            "active": true,
            "create_date": "2024-11-04T01:55:20.249549Z"
        }
    ],
    "swift_keys": [],
    "caps": [],
    "op_mask": "read, write, delete",
    "default_placement": "",
    "default_storage_class": "",
    "placement_tags": [],
    "bucket_quota": {
        "enabled": false,
        "check_on_raw": false,
        "max_size": -1,
        "max_size_kb": 0,
        "max_objects": -1
    },
    "user_quota": {
        "enabled": false,
        "check_on_raw": false,
        "max_size": -1,
        "max_size_kb": 0,
        "max_objects": -1
    },
    "temp_url_keys": [],
    "type": "rgw",
    "mfa_ids": [],
    "account_id": "",
    "path": "/",
    "create_date": "2024-11-04T01:55:20.249335Z",
    "tags": [],
    "group_ids": []
}
```

### 查看用户信息

```Bash
# 查看bigkang用户详情
radosgw-admin user info --uid bigkang


# 返回如下

{
    "user_id": "bigkang",
    "display_name": "bigkang",
    "email": "",
    "suspended": 0,
    "max_buckets": 1000,
    "subusers": [],
    "keys": [
        {
            "user": "bigkang",
            "access_key": "SUPBHAOZB2LBF613UWWY",
            "secret_key": "7prKiDJ8Erdqy2bk3J1SzuHih1eqO4r0p1h2kYKe",
            "active": true,
            "create_date": "2024-11-04T01:41:46.362637Z"
        }
    ],
    "swift_keys": [],
    "caps": [],
    "op_mask": "read, write, delete",
    "default_placement": "",
    "default_storage_class": "",
    "placement_tags": [],
    "bucket_quota": {
        "enabled": true,
        "check_on_raw": false,
        "max_size": -1024,
        "max_size_kb": 0,
        "max_objects": -1
    },
    "user_quota": {
        "enabled": true,
        "check_on_raw": false,
        "max_size": -1024,
        "max_size_kb": 0,
        "max_objects": -1
    },
    "temp_url_keys": [],
    "type": "rgw",
    "mfa_ids": [],
    "account_id": "",
    "path": "/",
    "create_date": "2024-11-04T01:41:46.361591Z",
    "tags": [],
    "group_ids": []
}
```

### 测试用户是否可用

```Bash
# 安装 s3cmd
 sudo apt -o Acquire::http::proxy="http://192.168.2.64:3128" install s3cmd
 
 
 # 查看Rgw网关
 sudo cephadm shell -- ceph orch ps
 
 # 找到RGW以及端口
rgw.rgw.cephcluster.hl-server-00.ifzmsm  hl-server-00  *:11801           running (3d)    10m ago   3d     110M        -  19.2.0   37996728e013  76010fb74219
rgw.rgw.cephcluster.hl-server-01.ybhgre  hl-server-01  *:11801           running (3d)    10m ago   3d     124M        -  19.2.0   37996728e013  8a42a58b2f8c

# 开始配置
sudo s3cmd --configure

New settings:
  Access Key: SUPBHAOZB2LBF613UWWY
  Secret Key: 7prKiDJ8Erdqy2bk3J1SzuHih1eqO4r0p1h2kYKe
  Default Region: CN
  S3 Endpoint: 192.168.2.200:11801
  DNS-style bucket+hostname:port template for accessing a bucket: ceph-oss
  Encryption password:
  Path to GPG program: /usr/bin/gpg
  Use HTTPS protocol: False
  HTTP Proxy server name:
  HTTP Proxy server port: 0

# 测试
sudo  s3cmd ls
```

### 测试上传功能

```Bash
# 创建文件
sudo echo "test ceph oss upload" > testUploadFile.txt

# 测试上传
sudo s3cmd put testUploadFile.txt s3://ceph-oss

# 看到进度条以及上传完成
upload: 'testUploadFile.txt' -> 's3://ceph-oss/testUploadFile.txt'  [1 of 1]
 21 of 21   100% in    3s     6.40 B/s  done

# 测试定义路径上传
sudo s3cmd put testUploadFile.txt s3://ceph-oss/test/txt/testUploadFile.txt


# 下载文件
sudo s3cmd get s3://ceph-oss/testUploadFile.txt ./download-1.txt

sudo s3cmd get s3://ceph-oss/test/txt/testUploadFile.txt ./download-2.txt
```

### 使用纠删码池（待定）

```Bash
ceph osd erasure-code-profile set rgw_ec_profile k=2 m=2


ceph osd pool create my_rgw_ec_pool 16 16 erasure rgw_ec_profile
ceph osd pool create my_rgw_index_pool 32 32 replicated
ceph osd pool create my_rgw_ec_ext_pool 32 32 replicated

ceph osd pool application enable my_rgw_ec_pool rgw
ceph osd pool application enable my_rgw_index_pool rgw
ceph osd pool application enable my_rgw_ec_ext_pool rgw



radosgw-admin realm create --rgw-realm=ec-realm --default

radosgw-admin zonegroup create --rgw-zonegroup=ec-zonegroup --rgw-realm=ec-realm --master --default

radosgw-admin zone create --rgw-zonegroup=ec-zonegroup --rgw-zone=ec-zone --master --default




# 添加placement
 radosgw-admin zonegroup placement add \
    --rgw-zonegroup=ec-zonegroup \
    --placement-id=ec-zone-placement
 
radosgw-admin zone placement add \
    --rgw-zone=ec-zone \
    --placement-id=ec-zone-placement \
    --data-pool=my_rgw_ec_pool \
    --index-pool=my_rgw_index_pool \
    --data-extra-pool=my_rgw_ec_ext_pool



sudo ceph orch apply rgw ceph-ec-oss \
    --port 11801 \
    --placement="hl-server-00,hl-server-01,hlserver02,hl-server-03" \
    --rgw-zone=ec-zone \
    --rgw-zonegroup=ec-zonegroup 




# 删除RGW
sudo ceph orch rm rgw.ceph-oss

# 删除RGW
ceph orch ps




ceph osd pool delete my_rgw_ec_pool my_rgw_ec_pool  --yes-i-really-really-mean-it

ceph osd pool delete default.rgw.buckets.index default.rgw.log  --yes-i-really-really-mean-it
ceph osd pool delete default.rgw.log default.rgw.log  --yes-i-really-really-mean-it
ceph osd pool delete default.rgw.control default.rgw.control  --yes-i-really-really-mean-it
ceph osd pool delete default.rgw.meta default.rgw.meta  --yes-i-really-really-mean-it
# 设置纠删码池
sudo cephadm shell -- ceph config set rgw rgw_default_data_pool my_ec_pool


ceph config set rgw rgw_default_data_pool my_rgw_ec_pool

ceph config set rgw.cephcluster rgw_default_data_pool my_ec_pool

sudo radosgw-admin zonegroup create --rgw-zonegroup=ec-zonegroup --master --default

sudo radosgw-admin zone create --rgw-zone=test-ec-zone --rgw-zonegroup=ec-zonegroup  --data-pool=my_ec_pool




sudo radosgw-admin pool add --pool=my_ec_pool --index-pool=my_ec_index_pool --data-pool=my_ec_data_pool --placement=default-placement


sudo ceph orch apply rgw rgw.cephcluster --port 11801 --placement="hl-server-00,hl-server-01"
```

### 测试上传下载命令

```Bash
# 上传 
sudo s3cmd put ceph19.2.0.tar.gz s3://ceph-oss/ceph19.2.0.tar.gz

60-80MB/s

# 下载
sudo s3cmd get s3://ceph-oss/ceph19.2.0.tar.gz ./dw-ceph19.2.0.tar.gz
200MB/s  - 220MB/s 


# 测试SSD写入
sudo dd if=ceph19.2.0.tar of=/SSD_4T/ceph/test-ceph19.2.0.tar.gz bs=1G oflag=direct

1303678464 bytes (1.3 GB, 1.2 GiB) copied, 0.599299 s, 2.2 GB/s


# 测试SSD读取
sudo dd if=/SSD_4T/ceph/ceph19.2.0.tar of=/dev/null bs=1G
1303678464 bytes (1.3 GB, 1.2 GiB) copied, 4.69954 s, 277 MB/s
1303678464 bytes (1.3 GB, 1.2 GiB) copied, 4.60307 s, 283 MB/s
1303678464 bytes (1.3 GB, 1.2 GiB) copied, 4.64126 s, 281 MB/s
```

## 数据同步OSS

```Bash
# 安装同步工具
 sudo apt -o Acquire::http::proxy="http://192.168.2.85:3128" install rclone
 
# 配置同步工具
sudo rclone config

# 开始同步
sudo rclone copy /SSD_4T/meituan/caseFile/1823/0494/2071/6339/291 test-sync:test-sync/caseFile/1823/0494/2071/6339/291 --progress


# 验证
sudo rclone ls test-sync:test-sync/caseFile/1823/0494

sudo rclone lsd test-sync:test-sync/caseFile/1823/0494
```

### 同步美团

```Bash
# 配置信息
sudo rclone config


# 开始同步
nohup sudo rclone copy /SSD_4T/meituan/caseFile meituan-materials:meituan-materials/caseFile --progress --ignore-existing > rclone-01.log 2>&1 &

tail -f rclone-01.log


# 同步02
nohup sudo rclone copy /HDD_data/meituan/meituan_case_files/caseFile meituan-materials:meituan-materials/caseFile --progress --ignore-existing > rclone.log 2>&1 &

tail -f -n 10 rclone.log



sudo rclone ls meituan-materials:meituan-materials/
```

### 同步京东

03

```Bash
 sudo apt -o Acquire::http::proxy="http://192.168.2.85:3128" install rclone
 
 
 NR1YPDYLU4SKTTOPCN3T
 
 lSBHO416e72VgeSykZdljfFaSXPFLs9VyfqSgqKw
 
 cargo-materials
 
 
 # 配置信息
sudo rclone config


sudo nohup rclone copy /SSD_DATA/cargo-jd/case-materials cargo-materials:cargo-materials/case-materials --progress --ignore-existing > rclone.log 2>&1 &
```

## prometheus

```Bash
# 开启prometheus组件
sudo ceph mgr module enable prometheus
sudo ceph mgr module enable alerts

# 查看服务
sudo ceph mgr services

# 查看配置
ceph config get mgr

ceph orch ps

# 设置配置组件
ceph config set mgr mgr/dashboard/PROMETHEUS_API_HOST http://192.168.2.201:9283
ceph config set mgr mgr/dashboard/ALERTMANAGER_API_HOST http://192.168.2.200:9094


# 配置GRAFANA  注意：使用https 需要先使用浏览器直接访问 https://192.168.2.200:3000 然后才能ceph直接查看，可能是证书原因
ceph config set mgr mgr/dashboard/GRAFANA_API_URL https://192.168.2.200:3000

ceph config get mgr 
```