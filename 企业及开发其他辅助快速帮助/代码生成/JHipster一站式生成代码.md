# 什么是JHipster？



# 安装JHipster？

## 不安装（网页构建）

​		 进入网址：[JHipster Online](https://start.jhipster.tech/)

​		 登录后根据提示新建项目即可

## NPM安装

​		请先安装NodeJs(版本 10.20.1 以及以上)以及JDK

​		然后使用命令：

```
npm install -g generator-jhipster
```

​		安装完毕后输入命令,出现版本即可

```
jhipster -V
```

## Docker安装

创建挂载目录用于存放文件，创建app和.m2

```
mkdir -p /docker/jhipster/{app,.m2}
```

拉取镜像

```
docker pull jhipster/jhipster
```

运行容器

```
docker run -td \
--name jhipster \
-v /docker/jhipster/app:/home/jhipster/app \
-v /docker/jhipster/.m2:/home/jhipster/.m2 \
-p 8080:8080 \
-p 9000:9000 \
-p 3001:3001 \
jhipster/jhipster
```

进入容器

```
docker exec -it --user root jhipster bash
```

进入app目录

```
cd /home/jhipster/app
```



# 创建项目

## 了解命令

我们输入 jhipster -h

即可查看相关的命令

```

Options:
  -V, --version                            输出版本号
  -d, --debug                              DEBUG启动
  -h, --help                               帮助菜单

Commands:
  app [options]                            [默认] 根据所选选项创建一个新的JHipster应用程序
  aws [options]                            将当前应用程序部署到Amazon Web服务
  aws-containers [options]                 使用ECS将当前应用程序部署到Amazon Web服务
  azure-app-service [options]              将当前应用程序部署到Azure应用程序服务
  azure-spring-cloud [options]             将当前应用程序部署到Azure Spring云
  ci-cd [options]                          为流行的持续集成/持续部署工具创建管道脚本
  cloudfoundry [options]                   生成带有特定清单的“deploy/cloudfoundry”文件夹，yml部署到云计算
  docker-compose [options]                 为所选应用程序创建所有必需的Docker部署配置
  entity [options] [name]                  创建一个新的JHipster实体:JPA实体、Spring服务器端组件和Angular客户端组件
  export-jdl [options] [jdlFile]           从现有实体创建一个JDL文件
  gae [options]                            将当前应用程序部署到谷歌应用程序引擎
  heroku [options]                         将当前应用程序部署到Heroku
  info [options]                           显示有关当前项目和系统的信息
  jdl|import-jdl [options] [jdlFiles...]   从参数中传递的JDL文件/内容创建实体。默认情况下，一切都是并行运行的。如果您想与控制台交互，请使用'——interactive'标志。
  
  Note: 																	 jhipster import-jdl与“jhipster jdk——跳过示例-存储库”相同
  kubernetes|k8s [options]                 将当前应用程序部署到Kubernetes
  kubernetes-helm|helm [options]           使用Helm包管理器将当前应用程序部署到Kubernetes
  kubernetes-knative|knative [options]     使用knative构造将当前应用程序部署到Kubernetes
  languages [options] [languages...]       从可用语言列表中选择语言。i18n文件将被复制到/webapp/i18n文件夹
  openshift [options]                      将当前应用程序部署到OpenShift
  spring-service|service [options] [name]  创建一个新的Spring服务bean
  spring-controller [options] [name]       创建一个新的Spring控制器
  openapi-client [options]                 从OpenAPI/Swagger定义生成java客户端代码
  upgrade [options]                        升级JHipster版本，并升级生成的应用程序
  upgrade-config [options]                 升级JHipster配置
  completion                               打印命令完成脚本
  help [command]                           显示命令的帮助
```

## 创建流程

​		首先新建一个项目到一个文件夹中

```
mkdir test-jhipster
cd test-jhipster
```

​		然后输入命令

```
jhipster
```

### 选择项目类型

​		我们可以看到这里有4个选择，我们可以按键盘上的上下键进行选择

![](http://yanxuan.nosdn.127.net/de76fdd2f918f784c6d0cfc51f0e769a.png)

```
  Monolithic application 							（简单单体应用，生成后前后端代码叠放到了一起）
  Microservice application 						（微服务应用）
  Microservice gateway 								（微服务网关）
  JHipster UAA server 								（微服务UAA认证服务）
```

​		选择我们相应的项目即可，然后按一下回车

### 创建简单单体应用

​		我们选择了第一个简单单体应用后进入下一个页面

​		这里它提示我们是否构建一个WebFlux（响应式流）应用，如果输入y的话则是使用，如果输入N的话就是传统的MVC（目前MVC使用比较多）项目。

![](http://yanxuan.nosdn.127.net/3c952735c36405c335d9e88ac0176d4e.png)

​		我们这里使用传统MVC，选择N

​		然后他就又会提示我们创建这个项目的项目名称，我们这里输入test_application然后回车,test_application是我们的项目名

​		![](http://yanxuan.nosdn.127.net/5ae133d7587a00bb69c5a7641fffd0bd.png)

​		接下来他就会提示我们输入包名了，这里我们输入com.kang.test.jhipster

![](http://yanxuan.nosdn.127.net/ec6a4f7f1095b77b12affaa7f4626a2f.png)

​		然后就会让我们选择是否使用注册表以及配置来监听应用，JVM使用等等资源的监控以及配置文件，我们这里不监控选择NO，回车

![](http://yanxuan.nosdn.127.net/ad5faad68f5c445d8906844327b4811d.png)

​		接下来就会让我们选择认证方式，分别有3种，我们这里选择JWT因为是简单项目

```
JWT     				基于JWT的token方式进行认证（无状态）
HTTP Session		基于Security的Session会话方式认证
Oauth2.0				采用Oauth2的认证方式
```

![](http://yanxuan.nosdn.127.net/d6cb9d856417f33d3505da0f25adfd4a.png)

​		然后就会让我们选择一个数据库类型，我们这里选择关系型数据库SQL，然后我们回车

![](http://yanxuan.nosdn.127.net/f3c8cbe6e205a24b139354fb2a3f7343.png)

​		然后选择MySQL，回车，这里是选择我们的生产数据库

​		![](http://yanxuan.nosdn.127.net/0496f569057ea9aab715a5254633fc83.png)

​		下面我们再选择开发环境dev数据库，这里同样选择MySQL		

​		![](http://yanxuan.nosdn.127.net/790adc60edb57717b8414b5a9dd2708b.png)

​		然后就到了选择我们的缓存方式，我们这里选择Ehcache进行实现，回车

![](http://yanxuan.nosdn.127.net/0cbc24742699a4e47a8e6c01b0b7b98e.png)

​		然后选择是否开启Hibernate的二级缓存，我们选择N不开启或者开启都行，然后回车

![](http://yanxuan.nosdn.127.net/3bd6fea5acc5c3bdc22ea6622054aa1c.png)

​		然后选择我们的构建方式，我这里选择的是Maven

![](/Users/bigkang/Library/Application Support/typora-user-images/image-20200709104930001.png)

​		下面就可以选择我们的拓展技术了，上下键滑动，空格选中（这里是多选），这里我们直选中最后面的API（Swagger）即可，然后回车

​		![](http://yanxuan.nosdn.127.net/7911af0bfff3ef79239b5a9c898b2f81.png)

​		再接下来就是选择我们的前端技术了，选择Angular或者React即可，这里我选择React

​		![](http://yanxuan.nosdn.127.net/12fd5532f6ffe5dcb3203c160e6cd972.png)

​		然后是选择生成的主题 这下面有很多种主题 https://bootswatch.com/		

​		这里我选择了自己感觉好看的Lux

![](http://yanxuan.nosdn.127.net/d4003b428758463d73da8b28a5e4d077.png)

​		然后就是选择一个Bootswatch变体Navbar主题，也在上方网站能看到

​		![](http://yanxuan.nosdn.127.net/5e041c93cf11996edfbd32ad948e8415.png)

​		然后我们在选择是否支持国际化，我们这里选择支持输入Y

​		



​		