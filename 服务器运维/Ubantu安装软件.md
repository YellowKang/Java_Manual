# 安装R语言

## 联网安装

修改镜像源

```
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial main restricted universe multiverse>>/etc/apt/sources.list
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse>>/etc/apt/sources.list
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse>>/etc/apt/sources.list
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-security main restricted universe multiverse>>/etc/apt/sources.list
```

更新镜像源

```
apt-get update
```



```
install.packages("ggplot2")
install.packages("rmongodb")
install.packages("mongolite")
install.packages("lubridate")
install.packages("dplyr")
install.packages("forecast")
install.packages("Cairo")



install.packages("BiocInstaller", repos="http://bioconductor.org/packages/2.14/bioc")

```

