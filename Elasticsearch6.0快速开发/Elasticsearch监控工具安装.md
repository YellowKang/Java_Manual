# cerebro安装

```
docker run -d -p 9000:9000 \
--restart=unless-stopped \
--name cerebro \
-v /etc/localtime:/etc/localtime \
-v cerebro:/opt/cerebro \
-h cerebro \
lmenezes/cerebro
```

目前介绍cerebro好用简单

