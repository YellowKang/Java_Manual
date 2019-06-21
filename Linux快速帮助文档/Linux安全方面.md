# 禁止使用Root用户ssh登陆

首先如果我们使用云服务器那么先给自己创建用户

如果不创建禁止了ssh就没法连了

```
创建用户
useradd bigkang
设置密码
passwd bigkang
输入两次密码
```

用户创建完成

我们禁止root用户ssh连接

```
vim /etc/ssh/sshd_config
找到PermitRootLogin yes，改为no
PermitRootLogin no

也可以使用命令一键修改
sed -i 's/\(PermitRootLogin\) yes/\1 no/' /etc/ssh/sshd_config

然后重启服务
systemctl restart sshd
```

然后就能禁止root远程登录了

# 检查服务器

## 检查用户

```
查看系统中的用户信息
cat /etc/passwd

列出具有超级权限的用户
cat /etc/passwd | awk -F: '$3==0'

列出具有登录shell的用户
cat /etc/passwd | grep '/bin/bash'
```

## 检查服务

```
查看当前默认开启运行的系统服务
chkconfig --list | grep "3:on"

不需要的服务就可以关闭了，关闭的命令是
chkconfig <servername> off
```

