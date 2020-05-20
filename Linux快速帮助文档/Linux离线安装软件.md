# 安装gcc



```
rpm -ivh lib64gmp3-4.3.1-1mdv2010.0.x86_64.rpm
rpm -ivh ppl-0.10.2-11.el6.x86_64.rpm
rpm -ivh cloog-ppl-0.15.7-1.2.el6.x86_64.rpm
rpm -ivh mpfr-2.4.1-6.el6.x86_64.rpm
rpm -ivh cpp-4.4.7-4.el6.x86_64.rpm --force
rpm -ivh kernel-headers-2.6.32-431.el6.x86_64.rpm
rpm -ivh glibc-headers-2.12-1.132.el6.x86_64.rpm --nodeps --force
rpm -ivh glibc-devel-2.12-1.132.el6.x86_64.rpm --force --nodeps
rpm -ivh gcc-4.4.7-4.el6.x86_64.rpm --force --nodeps
rpm -ivh libstdc++-devel-4.4.7-4.el6.x86_64.rpm --force --nodeps
rpm -ivh gcc-c++-4.4.7-4.el6.x86_64.rpm --force --nodeps
```

