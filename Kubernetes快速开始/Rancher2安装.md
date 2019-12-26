一键安装

```
docker run -d --name rancher2 --restart=unless-stopped -p 80:80 -p 443:443 rancher/rancher
```





```
docker ps| grep rancher-agent | grep -v grep | awk '{print "docker stop "$1}'|sh

docker ps -a| grep rancher-agent | grep -v grep | awk '{print "docker rm "$1}'|sh

docker images| grep rancher-agent | grep -v grep | awk '{print "docker rmi "$3}'|sh
```

