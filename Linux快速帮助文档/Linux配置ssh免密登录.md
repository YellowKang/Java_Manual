# 生成ssh

在需要访问的那台主机中生成，比如s1要访问s2免密登录。那么就在s2中生成

```
ssh-keygen -t rsa
```

# 获取客户端公钥

```
ssh-keygen -t rsa
cat /root/.ssh/authorized_keys
```

然后我们把它cv一下

# 再次进入服务主机

在s2中将s1的公钥放进去

```
vim /root/.ssh/authorized_keys 
```

然后我们再s1中就可以连接了

```
ssh  s2的ip地址
```

注意第一次连接会提示你是否保存信息，输入yes即可