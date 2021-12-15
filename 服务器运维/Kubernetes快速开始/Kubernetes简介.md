# 什么是Kubernetes？

​	首先，他是一个全新的基于容器技术的分布式架构领先方案。Kubernetes(k8s)是Google开源的容器集群管理系统（谷歌内部:Borg）。在Docker技术的基础上，为容器化的应用提供部署运行、资源调度、服务发现和动态伸缩等一系列完整功能，提高了大规模容器集群管理的便捷性。

　　Kubernetes是一个完备的分布式系统支撑平台，具有完备的集群管理能力，多扩多层次的安全防护和准入机制、多租户应用支撑能力、透明的服务注册和发现机制、內建智能负载均衡器、强大的故障发现和自我修复能力、服务滚动升级和在线扩容能力、可扩展的资源自动调度机制以及多粒度的资源配额管理能力。同时Kubernetes提供完善的管理工具，涵盖了包括开发、部署测试、运维监控在内的各个环节，Kubernetes是一个可移植的，可扩展的开源平台，用于管理容器化的工作负载和服务，可促进声明式配置和自动化。它拥有一个庞大且快速增长的生态系统。Kubernetes的服务，支持和工具广泛可用。

​	概述：是谷歌基于Borg开发的一款容器集群管理技术

​		    什么是容器集群管理：

​				我们的容器应用，例如SpringCloud微服务打成jar包之后我们是要做成Docker镜像进行运行的，那么我们在分布式的环境中我们需要一个工具来管理这些分布式的容器，所以诞生了Kubernetes



​		**传统部署时代：** 早期，组织在物理服务器上运行应用程序。无法为物理服务器中的应用程序定义资源边界，这会导致资源分配问题。例如，如果在物理服务器上运行多个应用程序，则可能会出现一个应用程序占用大部分资源的情况，结果，其他应用程序的性能将下降。解决方案是在不同的物理服务器上运行每个应用程序。但是，这并没有随着资源利用不足而扩展，并且组织维护许多物理服务器的成本很高。

​		**虚拟化部署时代：**作为解决方案，引入了虚拟化。它允许您在单个物理服务器的CPU上运行多个虚拟机（VM）。虚拟化允许在VM之间隔离应用程序，并提供安全级别，因为一个应用程序的信息不能被另一应用程序自由访问。虚拟化可以更好地利用物理服务器中的资源，并可以实现更好的可伸缩性，因为可以轻松地添加或更新应用程序，降低硬件成本等等。借助虚拟化，您可以将一组物理资源呈现为一组一次性虚拟机。每个VM都是一台完整的计算机，在虚拟化硬件之上运行所有组件，包括其自己的操作系统。

​		**容器部署时代：**容器类似于VM，但是它们具有轻松的隔离属性，可以在应用程序之间共享操作系统（OS）。因此，容器被认为是轻质的。与VM相似，容器具有自己的文件系统，CPU，内存，进程空间等的共享。由于它们与基础架构分离，因此可以跨云和OS分发进行移植。

​		容器之所以受欢迎，是因为它们提供了额外的好处，例如：



- 敏捷的应用程序创建和部署：与使用VM映像相比，容器映像创建的简便性和效率更高。
- 持续开发，集成和部署：通过快速轻松的回滚（由于图像不可更改），提供可靠且频繁的容器映像构建和部署。
- 开发和运营的关注点分离：在构建/发布时而不是在部署时创建应用程序容器映像，从而将应用程序与基础架构分离。
- 可观察性不仅可以显示操作系统级别的信息和指标，还可以显示应用程序的运行状况和其他信号。
- 跨开发，测试和生产的环境一致性：在便携式计算机上与在云中相同地运行。
- 云和操作系统分发的可移植性：可在Ubuntu，RHEL，CoreOS，本地，主要公共云以及其他任何地方运行。
- 以应用程序为中心的管理：提高抽象级别，从在虚拟硬件上运行操作系统到使用逻辑资源在操作系统上运行应用程序。
- 松散耦合，分布式，弹性，解放的微服务：应用程序被分解成较小的独立部分，并且可以动态部署和管理-而不是在一台大型单机上运行的整体堆栈。
- 资源隔离：可预测的应用程序性能。
- 资源利用：高效率和高密度。

# 为什么需要Kubernetes以及它可以做什么？

​		容器是捆绑和运行应用程序的好方法。在生产环境中，您需要管理运行应用程序的容器，并确保没有停机时间。例如，如果一个容器发生故障，则需要启动另一个容器。如果系统处理此行为，会不会更容易？

​		这就是Kubernetes的救援方法！Kubernetes为您提供了一个可弹性运行分布式系统的框架。它负责应用程序的扩展和故障转移，提供部署模式等。例如，Kubernetes可以轻松管理系统的Canary部署。

Kubernetes为您提供：

- **服务发现和负载平衡** Kubernetes可以使用DNS名称或使用其自己的IP地址公开容器。如果到容器的流量很高，Kubernetes可以负载平衡并分配网络流量，从而使部署稳定。
- **存储编排** Kubernetes允许您自动挂载您选择的存储系统，例如本地存储，公共云提供商等。
- **自动部署和回滚** 您可以使用Kubernetes描述已部署容器的所需状态，并且可以以受控的速率将实际状态更改为所需状态。例如，您可以自动化Kubernetes来为您的部署创建新容器，删除现有容器并将其所有资源用于新容器。
- **自动垃圾箱打包** 您为Kubernetes提供了一个节点集群，可用于运行容器化任务。您告诉Kubernetes每个容器需要多少CPU和内存（RAM）。Kubernetes可以将容器安装到您的节点上，以充分利用您的资源。
- **自我修复的** Kubernetes重启失败的容器，替换容器，杀死不响应用户定义的运行状况检查的容器，并在准备好服务之前不将其通告给客户端。
- **秘密和配置管理** Kubernetes使您可以存储和管理敏感信息，例如密码，OAuth令牌和SSH密钥。您可以部署和更新机密和应用程序配置，而无需重建容器映像，也无需在堆栈配置中公开机密。

# 什么不是Kubernetes？

​		Kubernetes不是一个传统的，包罗万象的PaaS（平台即服务）系统。由于Kubernetes在容器级别而不是硬件级别运行，因此它提供了PaaS产品共有的一些普遍适用的功能，例如部署，扩展，负载平衡，并允许用户集成其日志记录，监视和警报解决方案。但是，Kubernetes并不是单片的，并且这些默认解决方案是可选的和可插入的。Kubernetes提供了构建开发人员平台的基础，但是在重要的地方保留了用户的选择和灵活性。

Kubernetes：

- 不限制支持的应用程序类型。Kubernetes旨在支持极为多样化的工作负载，包括无状态，有状态和数据处理工作负载。如果应用程序可以在容器中运行，那么它应该可以在Kubernetes上很好地运行。
- 不部署源代码，也不构建您的应用程序。持续集成，交付和部署（CI / CD）工作流取决于组织的文化和偏好以及技术要求。
- 不提供应用程序级别的服务，例如中间件（例如，消息总线），数据处理框架（例如，Spark），数据库（例如，MySQL），缓存或集群存储系统（例如，Ceph）作为内置服务。这样的组件可以在Kubernetes上运行，和/或可以由Kubernetes上运行的应用程序通过诸如[Open Service Broker的](https://openservicebrokerapi.org/)可移植机制访问。
- 不指示日志记录，监视或警报解决方案。它提供了一些集成作为概念证明，并提供了收集和导出指标的机制。
- 不提供也不要求配置语言/系统（例如，Jsonnet）。它提供了一个声明性API，该声明性API可以被任意形式的声明性规范所针对。
- 不提供也不采用任何全面的机器配置，维护，管理或自我修复系统。
- 此外，Kubernetes不仅仅是一个编排系统。实际上，它消除了编排的需要。编排的技术定义是执行定义的工作流程：首先执行A，然后执行B，然后执行C。相反，Kubernetes包含一组独立的，可组合的控制过程，这些过程连续地将当前状态驱动到提供的所需状态。从A到C的方式无关紧要。集中控制也不是必需的。这使得系统更易于使用，并且功能更强大，更健壮，更具弹性和可扩展性。



# Kubernetes的核心概念有哪些

## Cluster（集群）

​		部署Kubernetes时，您将获得一个集群。Kubernetes集群由一组称为 [节点](https://kubernetes.io/docs/concepts/architecture/nodes/)，运行容器化的应用程序。每个群集至少有一个工作节点。工作节点托管 [豆荚](https://kubernetes.io/docs/concepts/workloads/pods/)是应用程序工作负载的组成部分。的 [控制平面](https://kubernetes.io/docs/reference/glossary/?all=true#term-control-plane)管理集群中的工作节点和Pod。在生产环境中，控制平面通常在多台计算机上运行，而群集通常在多个节点上运行，从而提供了容错能力和高可用性。本文档概述了拥有完整且有效的Kubernetes集群所需的各种组件。

​		Kubernetes集群示意图如下。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1604980817130.png)

## Master（主节点）

​	k8s集群的管理节点，负责管理集群，提供集群的资源数据访问入口。拥有Etcd存储服务（可选），运行Api Server进程，Controller Manager服务进程及Scheduler服务进程，关联工作节点Node。Kubernetes API server提供HTTP Rest接口的关键服务进程，是Kubernetes里所有资源的增、删、改、查等操作的唯一入口。也是集群控制的入口进程；Kubernetes Controller Manager是Kubernetes所有资源对象的自动化控制中心；Kubernetes Schedule是负责资源调度（Pod调度）的进程

​	概述：老大，管理Kubernetes的所有组件，控制整个集群环境

## Node （节点）

​		Node是Kubernetes集群架构中运行Pod的服务节点（亦叫agent或minion）。Node是Kubernetes集群操作的单元，用来承载被分配Pod的运行，是Pod运行的宿主机。关联Master管理节点，拥有名称和IP、系统资源信息。运行docker eninge服务，守护进程kunelet及负载均衡器kube-proxy. 

​		Kubernetes 通过将容器放入在节点（Node）上运行的 Pod 中来执行你的工作负载。 节点可以是一个虚拟机或者物理机器，取决于所在的集群配置。每个节点都包含用于运行 [Pod](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 所需要的服务，这些服务由 [控制面](https://kubernetes.io/zh/docs/reference/glossary/?all=true#term-control-plane)负责管理。

通常集群中会有若干个节点；而在一个学习用或者资源受限的环境中，你的集群中也可能 只有一个节点。

每个Node节点都运行着以下一组关键进程：

- kubelet：负责对Pod对于的容器的创建、启停等任务


- kube-proxy：实现Kubernetes Service的通信与负载均衡机制的重要组件


- Docker Engine（Docker）：Docker引擎，负责本机容器的创建和管理工作


　Node节点可以在运行期间动态增加到Kubernetes集群中，默认情况下，kubelet会想master注册自己，这也是Kubernetes推荐的Node管理方式，kubelet进程会定时向Master汇报自身情报，如操作系统、Docker版本、CPU和内存，以及有哪些Pod在运行等等，这样Master可以获知每个Node节点的资源使用情况，冰实现高效均衡的资源调度策略。 



## Pod 

​		运行于Node节点上，若干相关容器的组合。Pod内包含的容器运行在同一宿主机上，使用相同的网络命名空间、IP地址和端口，能够通过localhost进行通。Pod是Kurbernetes进行创建、调度和管理的最小单位，它提供了比容器更高层次的抽象，使得部署和管理更加灵活。一个Pod可以包含一个容器或者多个相关容器。

　　Pod其实有两种类型：普通Pod和静态Pod，后者比较特殊，它并不存在Kubernetes的etcd存储中，而是存放在某个具体的Node上的一个具体文件中，并且只在此Node上启动。普通Pod一旦被创建，就会被放入etcd存储中，随后会被Kubernetes Master调度到摸个具体的Node上进行绑定，随后该Pod被对应的Node上的kubelet进程实例化成一组相关的Docker容器冰启动起来，在。在默认情况下，当Pod里的某个容器停止时，Kubernetes会自动检测到这个问起并且重启这个Pod（重启Pod里的所有容器），如果Pod所在的Node宕机，则会将这个Node上的所有Pod重新调度到其他节点上。

​		那么一个Pod包含一个容器或者多个容器， 天生地为其成员容器提供了两种共享资源：[网络](https://kubernetes.io/zh/docs/concepts/workloads/pods/#pod-networking)和 [存储](https://kubernetes.io/zh/docs/concepts/workloads/pods/#pod-storage)。

### 生命周期

​		Pod 的生命周期。 Pod 遵循一个预定义的生命周期，起始于 `Pending` [阶段](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#pod-phase)，如果至少 其中有一个主要容器正常启动，则进入 `Running`，之后取决于 Pod 中是否有容器以 失败状态结束而进入 `Succeeded` 或者 `Failed` 阶段。

​		在 Pod 运行期间，`kubelet` 能够重启容器以处理一些失效场景。 在 Pod 内部，Kubernetes 跟踪不同容器的[状态](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#container-states) 并处理可能出现的状况。

​		在 Kubernetes API 中，Pod 包含规约部分和实际状态部分。 Pod 对象的状态包含了一组 [Pod 状况（Conditions）](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#pod-conditions)。 如果应用需要的话，你也可以向其中注入[自定义的就绪性信息](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#pod-readiness-gate)。

​		Pod 在其生命周期中只会被[调度](https://kubernetes.io/zh/docs/concepts/scheduling-eviction/)一次。 一旦 Pod 被调度（分派）到某个节点，Pod 会一直在该节点运行，直到 Pod 停止或者 被[终止](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#pod-termination)。

​		和一个个独立的应用容器一样，Pod 也被认为是相对临时性（而不是长期存在）的实体。 Pod 会被创建、赋予一个唯一的 ID（[UID](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/names/#uids)）， 并被调度到节点，并在终止（根据重启策略）或删除之前一直运行在该节点。

​		如果一个[节点](https://kubernetes.io/zh/docs/concepts/architecture/nodes/)死掉了，调度到该节点 的 Pod 也被计划在给定超时期限结束后[删除](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#pod-garbage-collection)。



​		Pod 的 `status` 字段是一个 [PodStatus](https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.19/#podstatus-v1-core) 对象，其中包含一个 `phase` 字段。

​		我们可以使用如下命令获取Pod

```sh
kubectl get pods
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1605322592083.png)

​		或者我们获取一个Pod的详细信息也能看到状态

```
kubectl describe pod nacos-0 | grep State
```



|        状态         |                             描述                             |
| :-----------------: | :----------------------------------------------------------: |
|  `Pending`（悬决）  | Pod 已被 Kubernetes 系统接受，但有一个或者多个容器尚未创建亦未运行。此阶段包括等待 Pod 被调度的时间和通过网络下载镜像的时间， |
| `Running`（运行中） | Pod 已经绑定到了某个节点，Pod 中所有的容器都已被创建。至少有一个容器仍在运行，或者正处于启动或重启状态。 |
| `Succeeded`（成功） |        Pod 中的所有容器都已成功终止，并且不会再重启。        |
|  `Failed`（失败）   | Pod 中的所有容器都已终止，并且至少有一个容器是因为失败终止。也就是说，容器以非 0 状态退出或者被系统终止。 |
|  `Unknown`（未知）  | 因为某些原因无法取得 Pod 的状态。这种情况通常是因为与 Pod 所在主机通信失败。 |



​		一个包含多个容器的 Pod 中包含一个用来拉取文件的程序和一个 Web 服务器， 均使用持久卷作为容器间共享的存储：如下所示

​		![](https://d33wubrfki0l68.cloudfront.net/aecab1f649bc640ebef1f05581bfcc91a48038c4/728d6/images/docs/pod.svg)



## Replication Controller

​	Replication Controller用来管理Pod的副本，保证集群中存在指定数量的Pod副本。集群中副本的数量大于指定数量，则会停止指定数量之外的多余容器数量，反之，则会启动少于指定数量个数的容器，保证数量不变。Replication Controller是实现弹性伸缩、动态扩容和滚动升级的核心。 

## Service 

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

## Label

​	　Kubernetes中的任意API对象都是通过Label进行标识，Label的实质是一系列的Key/Value键值对，其中key于value由用户自己指定。Label可以附加在各种资源对象上，如Node、Pod、Service、RC等，一个资源对象可以定义任意数量的Label，同一个Label也可以被添加到任意数量的资源对象上去。Label是Replication Controller和Service运行的基础，二者通过Label来进行关联Node上运行的Pod。

我们可以通过给指定的资源对象捆绑一个或者多个不同的Label来实现多维度的资源分组管理功能，以便于灵活、方便的进行资源分配、调度、配置等管理工作。

一些常用的Label如下：

- 版本标签："release":"stable","release":"canary"......
- 环境标签："environment":"dev","environment":"qa","environment":"production"
- 架构标签："tier":"frontend","tier":"backend","tier":"middleware"
- 分区标签："partition":"customerA","partition":"customerB"
- 质量管控标签："track":"daily","track":"weekly"

​	Label相当于我们熟悉的标签，给某个资源对象定义一个Label就相当于给它大了一个标签，随后可以通过Label Selector（标签选择器）查询和筛选拥有某些Label的资源对象，Kubernetes通过这种方式实现了类似SQL的简单又通用的对象查询机制。

# Kubernetes 组件

## 控制平面组件（Control Plane Components）

​		我们也称之为控制组件，也就是Master组件。

​		控制平面的组件对集群做出全局决策(比如调度)，以及检测和响应集群事件（例如，当不满足部署的 `replicas` 字段时，启动新的 [pod](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/)）。

​		控制平面组件可以在集群中的任何节点上运行。 然而，为了简单起见，设置脚本通常会在同一个计算机上启动所有控制平面组件，并且不会在此计算机上运行用户容器。 请参阅[构建高可用性集群](https://kubernetes.io/zh/docs/setup/production-environment/tools/kubeadm/high-availability/) 中对于多主机 VM 的设置示例。

​		控制平面的组件我们会找一台单独的机器来部署，我们习惯上把部署控制平面组件的机器称为master节点，以下都会用master节点来代替控制平面这个概念，master节点的组件能够对k8s的集群做出全局决策（例如，调度），以及检测和响应集群事件(例如，当部署的副本字段不满足时启动一个新的POD)。Master节点组件可以在k8s集群中的任何机器上运行。然而，为了简单起见，通常会在同一台机器上启动所有控制平面组件，这台机器上最好不运行其他的容器化程序，所以我们就把专门部署控制平面组件的集群称为master节点。

### kube-apiserver

​		API 服务器是 Kubernetes [控制面](https://kubernetes.io/zh/docs/reference/glossary/?all=true#term-control-plane)的组件， 该组件公开了 Kubernetes API。 API 服务器是 Kubernetes 控制面的前端。

​		Kubernetes API 服务器的主要实现是 [kube-apiserver](https://kubernetes.io/zh/docs/reference/command-line-tools-reference/kube-apiserver/)。 kube-apiserver 设计上考虑了水平伸缩，也就是说，它可通过部署多个实例进行伸缩。 你可以运行 kube-apiserver 的多个实例，并在这些实例之间平衡流量。

​		kube-apiserver是Kubernetes master节点的组件，它公开了Kubernetes API。 API服务是Kubernetes master节点的前端。Kubernetes API服务是通过kube-apiserver组件实现的，kube-apiserver被设计成可以进行自动扩缩容，你可以运行多个kube-apiserver组件，通过keepalive+lvs或者其他负载均衡策略在这些组件之间平衡流量。kube-apiserver提供了资源操作的唯一入口，并提供认证、授权、访问控制、API注册和发现等机制，负责接收、解析、处理请求。

### etcd

​		etcd 是兼具一致性和高可用性的键值数据库，可以作为保存 Kubernetes 所有集群数据的后台数据库，也可以应用与微服务的服务发现。

​		您的 Kubernetes 集群的 etcd 数据库通常需要有个备份计划。要了解 etcd 更深层次的信息，请参考 [etcd 文档](https://etcd.io/docs)。

​		etcd是一个key/value形式的键值存储，保存了整个kubernetes集群的状态，在kubernetes中使用etcd时，需要对etcd做备份，保证高可用。整个kubernetes系统中一共有两个服务需要用到etcd，用etcd来协同和存储配置，分别是：

​			（1）网络插件calico、对于其它网络插件也需要用到etcd存储网络的配置信息 
​			（2）kubernetes本身，包括各种对象的状态和元信息配置 

注意：网络插件操作etcd使用的是v2的API，而kubernetes操作etcd使用的v3的API，所以在下面我们执行etcdctl的时候需要设置ETCDCTL_API环境变量，该变量默认值为2，表示使用v2版本的etcd api，v3表示使用v3版本的etcd api

### kube-scheduler

​		主节点上的组件，该组件监视那些新创建的未指定运行节点的 Pod，并选择节点让 Pod 在上面运行。

​		调度决策考虑的因素包括单个 Pod 和 Pod 集合的资源需求、硬件/软件/策略约束、亲和性和反亲和性规范、数据位置、工作负载间的干扰和最后时限。

​		kube-scheduler是kubernetes master节点的组件，用来监视已经被创建但是没有调度到node节点的pod，然后选择一个node节点用来运行它，kube-scheduler主要是负责pod的调度，按照预定的调度策略（如亲和性，反亲和性等）将Pod调度到相应的机器上。

### kube-controller-manager

​		在主节点上运行[控制器](https://kubernetes.io/docs/admin/kube-controller-manager/)的组件。

​		从逻辑上讲，每个[控制器](https://kubernetes.io/docs/admin/kube-controller-manager/)都是一个单独的进程，但是为了降低复杂性，它们都被编译到同一个可执行文件，并在一个进程中运行。

这些控制器包括:

- 节点控制器（Node Controller）: 负责在节点出现故障时进行通知和响应。
- 副本控制器（Replication Controller）: 负责为系统中的每个副本控制器对象维护正确数量的 Pod。
- 端点控制器（Endpoints Controller）: 填充端点(Endpoints)对象(即加入 Service 与 Pod)。
- 服务帐户和令牌控制器（Service Account & Token Controllers）: 为新的命名空间创建默认帐户和 API 访问令牌.

​		控制器管理器，用来检测控制器健康状态的，控制器是负责维护集群的状态，检查pod的健康状态，比如故障检测、自动扩展、滚动更新等一些操作。

### cloud-controller-manager

​		云控制器管理器是 1.8 的 alpha 特性。在未来发布的版本中，这是将 Kubernetes 与任何其他云集成的最佳方式。

`cloud-controller-manager` 仅运行特定于云平台的控制回路。 如果你在自己的环境中运行 Kubernetes，或者在本地计算机中运行学习环境， 所部署的环境中不需要云控制器管理器。

​		与 `kube-controller-manager` 类似，`cloud-controller-manager` 将若干逻辑上独立的 控制回路组合到同一个可执行文件中，供你以同一进程的方式运行。 你可以对其执行水平扩容（运行不止一个副本）以提升性能或者增强容错能力。

下面的控制器都包含对云平台驱动的依赖：

- 节点控制器（Node Controller）: 用于在节点终止响应后检查云提供商以确定节点是否已被删除
- 路由控制器（Route Controller）: 用于在底层云基础架构中设置路由
- 服务控制器（Service Controller）: 用于创建、更新和删除云提供商负载均衡器

## Node 组件

​		节点组件在每个节点上运行，维护运行的 Pod 并提供 Kubernetes 运行环境。

### kubelet

​		一个在集群中每个节点上运行的代理。它保证容器都运行在 Pod 中。

​		kubelet 接收一组通过各类机制提供给它的 PodSpecs，确保这些 PodSpecs 中描述的容器处于运行状态且健康。kubelet 不会管理不是由 Kubernetes 创建的容器。

​		kubelet在k8s集群的每一个节点上都需要运行，属于节点组件，负责与master节点的apiserver进行通信的，接收到客户的请求，进行创建Pod，管理Pod，启动pod等相关操作

### kube-proxy

​		[kube-proxy](https://kubernetes.io/docs/reference/command-line-tools-reference/kube-proxy/) 是集群中每个节点上运行的网络代理,实现 Kubernetes [Service](https://kubernetes.io/zh/docs/concepts/services-networking/service/) 概念的一部分。

kube-proxy 维护节点上的网络规则。这些网络规则允许从集群内部或外部的网络会话与 Pod 进行网络通信。

如果操作系统提供了数据包过滤层并可用的话，kube-proxy会通过它来实现网络规则。否则，kube-proxy 仅转发流量本身。

​		k8s代理，是在群集中的每个节点上运行的网络代理，kube-proxy负责请求转发，一旦发现了某一个Service关联的Pod信息发生了改变（如IP、Port等），由Kube-Proxy就会把变化后的service转换成IPVS或IPtables规则中，完成对后端pod的负载均衡

### 容器运行时（Container Runtime）

​		容器运行环境是负责运行容器的软件。

​		Kubernetes 支持多个容器运行环境: [docker](https://kubernetes.io/zh/docs/reference/kubectl/docker-cli-to-kubectl/)、 [containerd](https://containerd.io/docs/)、[CRI-O](https://cri-o.io/docs/) 以及任何实现 [Kubernetes CRI (容器运行环境接口)](https://github.com/kubernetes/community/blob/master/contributors/devel/sig-node/container-runtime-interface.md)。

## 插件（Addons）

​		插件使用 Kubernetes 资源（[DaemonSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/daemonset/)、 [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/)等）实现集群功能。 因为这些插件提供集群级别的功能，插件中命名空间域的资源属于 `kube-system` 命名空间。

​		下面描述众多插件中的几种。有关可用插件的完整列表，请参见 [插件（Addons）](https://kubernetes.io/zh/docs/concepts/cluster-administration/addons/)。

### DNS

​		尽管其他插件都并非严格意义上的必需组件，但几乎所有 Kubernetes 集群都应该 有[集群 DNS](https://kubernetes.io/zh/docs/concepts/services-networking/dns-pod-service/)， 因为很多示例都需要 DNS 服务。

​		集群 DNS 是一个 DNS 服务器，和环境中的其他 DNS 服务器一起工作，它为 Kubernetes 服务提供 DNS 记录。

​		Kubernetes 启动的容器自动将此 DNS 服务器包含在其 DNS 搜索列表中。



​		coredns:

​		k8s1.11之前使用的是kubedns，1.11之后才有coredns，coredns是一个DNS服务器，能够为 Kubernetes services提供 DNS记录。

### Web 界面（仪表盘）

​		我们也可以称之为Kubernetes监控工具。

​		[Dashboard](https://kubernetes.io/zh/docs/tasks/access-application-cluster/web-ui-dashboard/) 是K ubernetes 集群的通用的、基于 Web 的用户界面。 它使用户可以管理集群中运行的应用程序以及集群本身并进行故障排除。

​		Dashboard是k8s集群的一个web ui界面，通过这个界面可以对k8s资源进行操作，如创建pod，创建存储，创建网络等，也可以监控pod和节点资源使用情况。

### 容器资源监控

​		主要用于监控容器。

​		[容器资源监控](https://kubernetes.io/zh/docs/tasks/debug-application-cluster/resource-usage-monitoring/) 将关于容器的一些常见的时间序列度量值保存到一个集中的数据库中，并提供用于浏览这些数据的界面。	

​		要扩展应用程序并提供可靠的服务，你需要了解应用程序在部署时的行为。 你可以通过检测容器检查 Kubernetes 集群中的应用程序性能， [Pods](https://kubernetes.io/zh/docs/concepts/workloads/pods), [服务](https://kubernetes.io/zh/docs/concepts/services-networking/service/) 和整个集群的特征。 Kubernetes 在每个级别上提供有关应用程序资源使用情况的详细信息。 此信息使你可以评估应用程序的性能，以及在何处可以消除瓶颈以提高整体性能。

在 Kubernetes 中，应用程序监控不依赖单个监控解决方案。 在新集群上，你可以使用[资源度量](https://kubernetes.io/zh/docs/tasks/debug-application-cluster/resource-usage-monitoring/#resource-metrics-pipeline)或 [完整度量](https://kubernetes.io/zh/docs/tasks/debug-application-cluster/resource-usage-monitoring/#full-metrics-pipeline)管道来收集监视统计信息。

​		一个完整度量管道可以让你访问更丰富的度量。 Kubernetes 还可以根据集群的当前状态，使用 Pod 水平自动扩缩器等机制， 通过自动调用扩展或调整集群来响应这些度量。 监控管道从 kubelet 获取度量值，然后通过适配器将它们公开给 Kubernetes， 方法是实现 `custom.metrics.k8s.io` 或 `external.metrics.k8s.io` API。

​		[Prometheus](https://prometheus.io/) 是一个 CNCF 项目，可以原生监控 Kubernetes、 节点和 Prometheus 本身。 完整度量管道项目不属于 CNCF 的一部分，不在 Kubernetes 文档的范围之内。

​		监控系统，可以对kubernetes集群本身的组件监控，也可对物理节点，容器做监控，对监控到的超过报警阀值的数据进行报警，这个报警会发送到指定的目标，如钉钉，微信，qq，slack等。

### 集群层面日志

​		[集群层面日志](https://kubernetes.io/zh/docs/concepts/cluster-administration/logging/) 机制负责将容器的日志数据 保存到一个集中的日志存储中，该存储能够提供搜索和浏览接口。

​		日志管理系统，可以对物理节点和容器的日志进行统一收集，把收集到的数据在kibana界面展示，kibana提供按指定条件搜索和过滤日志。

# 高可用K8s集群

​		k8s的物理结构是master/node模式,架构图如下所示。

​		master一般是三个节点或者五个节点做高可用，根据集群规模来定，master高可用指的是对apiserver做高可用或者对master的物理节点做高可用。

​		node可以有多个节点，专门用来部署应用的。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1639474218098.png)
