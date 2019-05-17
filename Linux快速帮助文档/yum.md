使用工具更新yum源

下载

```
 yum install -y yum-utils device-mapper-persistent-data lvm2
```

更新yum源

```
 yum-config-manager --add-repo http://mirrors.aliyun.com/repo/Centos-7.repo
```



搜索yum源

```
yum search docker-ce --showduplicates | sort -r
```