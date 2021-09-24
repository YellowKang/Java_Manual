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
#PermitRootLogin yes 	 #禁止root远程登录
PasswordAuthentication no #禁止使用基于口令认证的方式登陆（所有用户）
PubkeyAuthentication yes #允许使用基于密钥认证的方式登陆
```

如果想要使用密码登录并且只限制root'的密码登录采用如下

```
#如果只想禁用掉root的远程密码登录，则使用下面这项，并且将上面设置为允许远程口令
#禁止root使用密码登录只能使用秘钥
PermitRootLogin prohibit-password
```

注意操作

```
#也可以使用命令一键修改，禁止root远程登录，！！！！！！！！！注意，使用后root无法远程登录，检查是否有其他用户能登陆后执行
sed -i 's/\(PermitRootLogin\) yes/\1 no/' /etc/ssh/sshd_config

#一键禁止root使用密码登录只能使用秘钥
sed -i 's/\(PermitRootLogin\) yes/\1 prohibit-password/' /etc/ssh/sshd_config

#一键禁止使用密码登录
sed -i 's/\(PasswordAuthentication\) yes/\1 no/' /etc/ssh/sshd_config
然后重启服务
```

```
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

# 升级OpenSSH

## 下载包

此次升级为

```
https://www.openssl.org/source/old/1.0.2/openssl-1.0.2o.tar.gz
https://fastly.cdn.openbsd.org/pub/OpenBSD/OpenSSH/portable/openssh-7.7p1.tar.gz
http://www.zlib.net/zlib-1.2.11.tar.gz
```



```
tar xf zlib-1.2.11.tar.gz
cd zlib-1.2.11
./configure --prefix=/usr/local/zlib
make
make install
make clean
./configure --shared
make test
make install
cp zutil.h /usr/local/include
cp zutil.c /usr/local/include
cd ..
```

```
tar xf openssl-1.0.2o.tar.gz
cd openssl-1.0.2o 
./config shared zlib
make 
make install
mv /usr/bin/openssl /usr/bin/openssl.bak 
mv /usr/include/openssl /usr/include/openssl.bak 
 
ln -s /usr/local/ssl/bin/openssl /usr/bin/openssl 
ln -s /usr/local/ssl/include/openssl /usr/include/openssl 
 
echo "/usr/local/ssl/lib" > /etc/ld.so.conf.d/openssl.conf

ldconfig  
 
openssl version -a
cd ..
```

```
mv /etc/init.d/ssh /etc/init.d/ssh.old
cp -r /etc/ssh /etc/ssh.old
 
apt-get remove -y openssh-server openssh-client
tar xf openssh-7.7p1.tar.gz
cd openssh-7.7p1
./configure --prefix=/usr --sysconfdir=/etc/ssh --with-zlib --without-openssl-header-check --with-ssl-dir=/usr/bin/openssl  --with-privsep-path=/var/
lib/sshd
make && make install
```





```
apt-get install -y gcc
apt-get install -y make
cd /home/itsm/
tar xf zlib-1.2.11.tar.gz
cd zlib-1.2.11
./configure
make
make install
cd ..
tar -zxvf openssl-1.1.1f.tar.gz
cd openssl-1.1.1f
./config shared zlib
make
make install
cd ..
mv /usr/bin/openssl /usr/bin/openssl.bak
ln -s /usr/local/ssl/bin/openssl /usr/bin/openssl
ln -s /usr/local/ssl/include/openssl /usr/include/openssl 
echo "/usr/local/ssl/lib" >> /etc/ld.so.conf
/sbin/ldconfig

tar xf openssh-8.1p1.tar.gz && cd openssh-8.1p1/
cp /etc/init.d/ssh /etc/init.d/ssh.old && cp -r /etc/ssh /etc/ssh.old


./configure --prefix=/usr --sysconfdir=/etc/ssh --with-zlib --without-openssl-header-check --with-ssl-dir=/usr/local/openssl  --with-privsep-path=/var/lib/sshd --with-openssl-libraries=/usr/local/ssl/bin/openssl



make
make install
ssh -V
```

```
ssh -V
scp /home/itsm/* 192.168.1.53:/home/itsm/
```

```
mv /usr/bin/openssl.bak /usr/bin/openssl
```





```
mkdir /offlinePackage
cp -r /var/cache/apt/archives  /offlinePackage
dpkg-scanpackages /offlinePackage/ /dev/null |gzip >/offlinePackage/Packages.gz -r
cp /offlinePackage/Packages.gz /offlinePackage/archives/Packages.gz
tar cvzf /offlinePackage.tar.gz /offlinePackage/
```





```
cp /etc/apt/sources.list /etc/apt/sources.list.back
```



```
echo "deb file:/// offlinePackage/" > /etc/apt/sources.list
cd /
tar -zxvf off.tar.gz
apt-get update
apt-get -f install

```





```
sudo dpkg -i *deb
sudo dpkg --force-depends -i *deb
```



```
apt-get update
apt-get upgrade
```

