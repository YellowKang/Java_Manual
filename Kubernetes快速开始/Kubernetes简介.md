# 1、什么是Kubernetes？

​	首先，他是一个全新的基于容器技术的分布式架构领先方案。Kubernetes(k8s)是Google开源的容器集群管理系统（谷歌内部:Borg）。在Docker技术的基础上，为容器化的应用提供部署运行、资源调度、服务发现和动态伸缩等一系列完整功能，提高了大规模容器集群管理的便捷性。

　　Kubernetes是一个完备的分布式系统支撑平台，具有完备的集群管理能力，多扩多层次的安全防护和准入机制、多租户应用支撑能力、透明的服务注册和发现机制、內建智能负载均衡器、强大的故障发现和自我修复能力、服务滚动升级和在线扩容能力、可扩展的资源自动调度机制以及多粒度的资源配额管理能力。同时Kubernetes提供完善的管理工具，涵盖了包括开发、部署测试、运维监控在内的各个环节



​	概述：是谷歌基于Borg开发的一款容器集群管理技术

​		    什么是容器集群管理：

​				我们的容器应用，例如SpringCloud微服务打成jar包之后我们是要做成Docker镜像进行运行的，那么我们在分布式的环境中我们需要一个工具来管理这些分布式的容器，所以诞生了Kubernetes

# 2、Kubernetes的核心概念有哪些

### Master

​	k8s集群的管理节点，负责管理集群，提供集群的资源数据访问入口。拥有Etcd存储服务（可选），运行Api Server进程，Controller Manager服务进程及Scheduler服务进程，关联工作节点Node。Kubernetes API server提供HTTP Rest接口的关键服务进程，是Kubernetes里所有资源的增、删、改、查等操作的唯一入口。也是集群控制的入口进程；Kubernetes Controller Manager是Kubernetes所有资源对象的自动化控制中心；Kubernetes Schedule是负责资源调度（Pod调度）的进程

​	概述：老大，管理Kubernetes的所有组件，控制整个集群环境

### Node 

​	Node是Kubernetes集群架构中运行Pod的服务节点（亦叫agent或minion）。Node是Kubernetes集群操作的单元，用来承载被分配Pod的运行，是Pod运行的宿主机。关联Master管理节点，拥有名称和IP、系统资源信息。运行docker eninge服务，守护进程kunelet及负载均衡器kube-proxy. 



​	每个Node节点都运行着以下一组关键进程

​	kubelet：负责对Pod对于的容器的创建、启停等任务

​	kube-proxy：实现Kubernetes Service的通信与负载均衡机制的重要组件

​	Docker Engine（Docker）：Docker引擎，负责本机容器的创建和管理工作



　Node节点可以在运行期间动态增加到Kubernetes集群中，默认情况下，kubelet会想master注册自己，这也是Kubernetes推荐的Node管理方式，kubelet进程会定时向Master汇报自身情报，如操作系统、Docker版本、CPU和内存，以及有哪些Pod在运行等等，这样Master可以获知每个Node节点的资源使用情况，冰实现高效均衡的资源调度策略。 

### Pod 

​	运行于Node节点上，若干相关容器的组合。Pod内包含的容器运行在同一宿主机上，使用相同的网络命名空间、IP地址和端口，能够通过localhost进行通。Pod是Kurbernetes进行创建、调度和管理的最小单位，它提供了比容器更高层次的抽象，使得部署和管理更加灵活。一个Pod可以包含一个容器或者多个相关容器。

　　Pod其实有两种类型：普通Pod和静态Pod，后者比较特殊，它并不存在Kubernetes的etcd存储中，而是存放在某个具体的Node上的一个具体文件中，并且只在此Node上启动。普通Pod一旦被创建，就会被放入etcd存储中，随后会被Kubernetes Master调度到摸个具体的Node上进行绑定，随后该Pod被对应的Node上的kubelet进程实例化成一组相关的Docker容器冰启动起来，在。在默认情况下，当Pod里的某个容器停止时，Kubernetes会自动检测到这个问起并且重启这个Pod（重启Pod里的所有容器），如果Pod所在的Node宕机，则会将这个Node上的所有Pod重新调度到其他节点上。

### Replication Controller

​	Replication Controller用来管理Pod的副本，保证集群中存在指定数量的Pod副本。集群中副本的数量大于指定数量，则会停止指定数量之外的多余容器数量，反之，则会启动少于指定数量个数的容器，保证数量不变。Replication Controller是实现弹性伸缩、动态扩容和滚动升级的核心。 

### Service 

​	Service定义了Pod的逻辑集合和访问该集合的策略，是真实服务的抽象。Service提供了一个统一的服务访问入口以及服务代理和发现机制，关联多个相同Label的Pod，用户不需要了解后台Pod是如何运行。

外部系统访问Service的问题

　　首先需要弄明白Kubernetes的三种IP这个问题

　　　　Node IP：Node节点的IP地址

　　　　Pod IP： Pod的IP地址

　　　　Cluster IP：Service的IP地址

　　首先,Node IP是Kubernetes集群中节点的物理网卡IP地址，所有属于这个网络的服务器之间都能通过这个网络直接通信。这也表明Kubernetes集群之外的节点访问Kubernetes集群之内的某个节点或者TCP/IP服务的时候，必须通过Node IP进行通信

　　其次，Pod IP是每个Pod的IP地址，他是Docker Engine根据docker0网桥的IP地址段进行分配的，通常是一个虚拟的二层网络。

　　最后Cluster IP是一个虚拟的IP，但更像是一个伪造的IP网络，原因有以下几点

​	Cluster IP仅仅作用于Kubernetes Service这个对象，并由Kubernetes管理和分配P地址

​	Cluster IP无法被ping，他没有一个“实体网络对象”来响应

​	Cluster IP只能结合Service Port组成一个具体的通信端口，单独的Cluster IP不具备通信的基础，并且他们属于Kubernetes集群这样一个封闭的空间。

Kubernetes集群之内，Node IP网、Pod IP网于Cluster IP网之间的通信，采用的是Kubernetes自己设计的一种编程方式的特殊路由规则。 

### Label

​	　Kubernetes中的任意API对象都是通过Label进行标识，Label的实质是一系列的Key/Value键值对，其中key于value由用户自己指定。Label可以附加在各种资源对象上，如Node、Pod、Service、RC等，一个资源对象可以定义任意数量的Label，同一个Label也可以被添加到任意数量的资源对象上去。Label是Replication Controller和Service运行的基础，二者通过Label来进行关联Node上运行的Pod。

我们可以通过给指定的资源对象捆绑一个或者多个不同的Label来实现多维度的资源分组管理功能，以便于灵活、方便的进行资源分配、调度、配置等管理工作。

一些常用的Label如下：

- 版本标签："release":"stable","release":"canary"......
- 环境标签："environment":"dev","environment":"qa","environment":"production"
- 架构标签："tier":"frontend","tier":"backend","tier":"middleware"
- 分区标签："partition":"customerA","partition":"customerB"
- 质量管控标签："track":"daily","track":"weekly"

​	Label相当于我们熟悉的标签，给某个资源对象定义一个Label就相当于给它大了一个标签，随后可以通过Label Selector（标签选择器）查询和筛选拥有某些Label的资源对象，Kubernetes通过这种方式实现了类似SQL的简单又通用的对象查询机制。

# 3、主组件有哪些

### etcd

​	高可用存储共享配置和服务发现，作为与minion机器上的flannel配套使用，作用是使每台 minion上运行的docker拥有不同的ip段，最终目的是使不同minion上正在运行的docker containner都有一个与别的任意一个containner（别的minion上运行的docker containner）不一样的IP地址。 

### flannel

​	网络结构支持 

### kube-apiserver

​	不论通过kubectl还是使用remote api 直接控制，都要经过apiserver 

### kube-controller-manager

​	对replication controller, endpoints controller, namespace controller, and serviceaccounts controller的循环控制，与kube-apiserver交互，保证这些controller工作 

### kube-scheduler 

​	Kubernetes scheduler的作用就是根据特定的调度算法将pod调度到指定的工作节点（minion）上，这一过程也叫绑定（bind) 

### kubelet 

​	Kubelet运行在Kubernetes Minion Node上. 它是container agent的逻辑继任者 

### kube-proxy 

​	kube-proxy是kubernetes 里运行在minion节点上的一个组件, 它起的作用是一个服务代理的角色 