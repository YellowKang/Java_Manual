# 什么是confluence？

​		Confluence是一个专业的企业知识管理与协同软件，也可以用于构建企业wiki。使用简单，但它强大的编辑和站点管理特征能够帮助团队成员之间共享信息、文档协作、集体讨论，信息推送。

# 安装Confluence

## Docker安装

​		官网地址：https://hub.docker.com/r/atlassian/confluence-server

```
docker run -itd \
--name="confluence" \
-p 8090:8090 \
-p 8091:8091 \
-v /docker/confluence/data:/var/atlassian/application-data/confluence \
atlassian/confluence-server:6.0.7-alpine
```