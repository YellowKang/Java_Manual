​		中文官网地址：[点击进入](https://kubernetes.io/zh/)

# 资源

​		我们知道K8s主要是用于容器编排，那么我们是怎么样对容器进行编排的呢？

​		我们通过配置文件进行管理，通常我们通过Yaml格式或者Json格式的文件或者资源进行管理，Pod资源格式如下：

​		以官网中的Nginx的Pod创建文件，我们将资源大概分为了4部分，分别是：

- ​				 apiVersion		对各个Api以及版本控制，用于申明API以及版本

- ​							kind        类型，针对于某一个API版本，那么我们创建的资源也是有不同的类型的

- ​				  metadata        元数据，用于存储定义资源的名称，命名空间等等容器的标识元数据

- ​						  spec          清单，清单用于表示对资源的相关的定义，如信息镜像，网络等等

``` yaml
# api版本
apiVersion: apps/v1
# 类型
kind: Deployment
# 元数据
metadata:
  name: nginx-deployment
  labels:
    app: nginx
# 清单
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
```

## apiVersion

​		对于官网地址如下

```sh
https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#objectmeta-v1-meta
```

​		

# RBAC

​		官网地址：[点击进入](https://kubernetes.io/docs/reference/access-authn-authz/rbac/)

​		基于角色的访问控制（RBAC）是一种根据组织内各个用户的角色来调节对计算机或网络资源的访问的方法。RBAC授权使用 `rbac.authorization.k8s.io` [API组](https://kubernetes.io/docs/concepts/overview/kubernetes-api/#api-groups) 驱动授权决策，使您可以通过Kubernetes API动态配置策略。要启用RBAC，请启动 [API服务器](https://kubernetes.io/docs/concepts/overview/components/#kube-apiserver) 将`--authorization-mode`标志设置为以逗号分隔的列表，其中包括`RBAC`；例如：

```sh
kube-apiserver --authorization-mode=Example,RBAC --other-options --more-options
```

​		RBAC API声明了四种Kubernetes对象：

- ​					Role（角色）

- ​					ClusterRole（集群角色）

- ​					RoleBinding（角色绑定）

- ​					ClusterRoleBinding（集群角色绑定）



​		RBAC*角色*或*ClusterRole*包含代表一组权限的规则。权限纯粹是累加的（没有“拒绝”规则），角色始终在特定对象内设置权限 [命名空间](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces); 创建角色时，必须指定其所属的名称空间。

​		相反，ClusterRole是非命名空间资源。资源具有不同的名称（Role和ClusterRole），因为Kubernetes对象始终必须命名空间或不命名空间。不能两者兼有。

## Role（角色）

​		创建角色示例如下：

​		这是“默认”名称空间中的示例角色，可用于授予对POD的读取访问权限。

```yaml
# api版本
apiVersion: rbac.authorization.k8s.io/v1
# 类型
kind: Role
# 元数据
metadata:
  namespace: default
  name: pod-reader
# 权限
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "watch", "list"]
```

## ClusterRole（集群角色）

​		可以使用ClusterRole授予与角色相同的权限。因为ClusterRoles是集群范围的，所以您也可以使用它们来授予对以下内容的访问权限：

​		集群范围内的资源（例如 [节点](https://kubernetes.io/docs/concepts/architecture/nodes/)）

​		非资源端点（如`/healthz`）

​		所有命名空间中的命名空间资源（例如Pod）例如：您可以使用ClusterRole允许特定用户运行 `kubectl get pods --all-namespaces`。

​		这是一个ClusterRole的示例，可用于授予对以下内容的读取访问权限，那么这个读取权限表示我们可以读取[Sercet](https://kubernetes.io/docs/concepts/configuration/secret/)：		

```yaml
# api版本
apiVersion: rbac.authorization.k8s.io/v1
# 类型
kind: ClusterRole
# 元数据
metadata:
	# 名称
  name: secret-reader
rules:
# api组
- apiGroups: [""]
	# 操作的资源
  resources: ["secrets"]
  # 操作的权限
  verbs: ["get", "watch", "list"]
```

​		角色或ClusterRole对象的名称必须是有效的安全地编码为路径段。换句话说，名称不能为“。” 或“ ..”，并且名称中不能包含“ /”或“％”。

​		

## RoleBinding（角色绑定）

​		角色绑定向一个或一组用户授予在角色中定义的权限。它包含一个*主题*列表（用户，组或服务帐户），以及对所授予角色的引用。RoleBinding授予特定命名空间中的权限，而ClusterRoleBinding授予在群集范围内访问的权限。

​		RoleBinding可以引用同一名称空间中的任何Role。或者，RoleBinding可以引用ClusterRole并将该ClusterRole绑定到RoleBinding的名称空间。如果要将ClusterRole绑定到群集中的所有名称空间，请使用ClusterRoleBinding。

​		RoleBinding或ClusterRoleBinding对象的名称必须是有效的 [路径段名称](https://kubernetes.io/docs/concepts/overview/working-with-objects/names#path-segment-names)。

​		下面是一个绑定角色的示例：

```yaml
# api版本
apiVersion: rbac.authorization.k8s.io/v1
# 资源类型
kind: RoleBinding
# 元数据，名称read-pods，命名空间default
metadata:
  name: read-pods
  namespace: default
# 主题，我们类型为用户，名字为jane，简单的来说就是给jane用户添加个角色
subjects:
- kind: User
  name: jane
  apiGroup: rbac.authorization.k8s.io
# 角色应用，pod-reader，前面创建的Role对Pod可以读取
roleRef:
  kind: Role
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

​		RoleBinding也可以引用ClusterRole，以将那个ClusterRole中定义的权限授予RoleBinding命名空间中的资源。这种参考使您可以在整个集群中定义一组通用角色，然后在多个名称空间中重用它们。



## ClusterRoleBinding（集群角色绑定）

​		要在整个群集上授予权限，可以使用ClusterRoleBinding。以下ClusterRoleBinding允许“管理器”组中的任何用户读取任何名称空间中的机密。

```yaml
# api版本
apiVersion: rbac.authorization.k8s.io/v1
# 类型
kind: ClusterRoleBinding
# 元数据
metadata:
  name: read-secrets-global
# 主题
subjects:
- kind: Group
  name: manager
  apiGroup: rbac.authorization.k8s.io
# 角色引用
roleRef:
  kind: ClusterRole
  name: secret-reader
  apiGroup: rbac.authorization.k8s.io
```



# 容器

## 镜像

​		官网地址：[点击进入](https://kubernetes.io/zh/docs/concepts/containers/images/)

​		当你最初创建一个 [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/)、 [StatefulSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/statefulset/)、Pod 或者其他包含 Pod 模板的对象时，如果没有显式设定的话，Pod 中所有容器的默认镜像 拉取策略是 `IfNotPresent`。这一策略会使得 [kubelet](https://kubernetes.io/docs/reference/generated/kubelet) 在镜像已经存在的情况下直接略过拉取镜像的操作。

​		容器的 **imagePullPolicy** 和镜像的标签会影响 kubelet 尝试拉取（下载）指定的镜像。以下列表包含了 imagePullPolicy 可以设置的值，拉取容器镜像有如下三种策略：

​				**IfNotPresent**：如果节点的镜像不存在就进行镜像拉取，如果存在就使用这个镜像进行启动。

​				**Always**：每当 kubelet 启动一个容器时，kubelet 会查询容器的镜像仓库， 将名称解析为一个镜像[摘要](https://docs.docker.com/engine/reference/commandline/pull/#pull-an-image-by-digest-immutable-identifier)。 如果 kubelet 有一个容器镜像，并且对应的摘要已在本地缓存，kubelet 就会使用其缓存的镜像； 否则，kubelet 就会使用解析后的摘要拉取镜像，并使用该镜像来启动容器。（查询镜像仓库Sha256，如果本地有镜像判断Sha256是否一致，如果一致则启动，不一致继续pull然后启动，如果本地没有镜像则拉取最新）

​			**Never**：Kubelet 不会尝试获取镜像。如果镜像已经以某种方式存在本地， kubelet 会尝试启动容器；否则，会启动失败。 更多细节见[提前拉取镜像](https://kubernetes.io/zh/docs/concepts/containers/images/#pre-pulled-images)。（不会去拉取镜像，如果本地没有则直接容器启动失败）

​		**说明：**

​				在生产环境中部署容器时，你应该避免使用 `:latest` 标签，因为这使得正在运行的镜像的版本难以追踪，并且难以正确地回滚。相反，应指定一个有意义的标签，如 `v1.42.0`。

​		**默认镜像拉取策略**

​		当你（或控制器）向 API 服务器提交一个新的 Pod 时，你的集群会在满足特定条件时设置 `imagePullPolicy `字段：

- 如果你省略了 `imagePullPolicy` 字段，并且容器镜像的标签是 `:latest`， `imagePullPolicy` 会自动设置为 `Always`。
- 如果你省略了 `imagePullPolicy` 字段，并且没有指定容器镜像的标签， `imagePullPolicy` 会自动设置为 `Always`。
- 如果你省略了 `imagePullPolicy` 字段，并且为容器镜像指定了非 `:latest` 的标签， `imagePullPolicy` 就会自动设置为 `IfNotPresent`。

## 容器资源管理

​		限制容器资源CPU等，官网地址：[点击进入](https://kubernetes.io/zh/docs/concepts/configuration/manage-resources-containers/)

​		

# POD

## Pod 的生命周期

​		官网地址，[点击进入](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/)

## Pod卷存储

​		我们知道容器中我们肯定需要将文件卷挂载到我们的固定物理磁盘防止丢失，如果我们把文件放入POD中，那么POD删除后这部分的数据就会丢失了，那么我们怎么持久化我们的物理磁盘呢？Kubernetes 支持很多类型的卷。 [Pod](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 可以同时使用任意数目的卷类型。 临时卷类型的生命周期与 Pod 相同，但持久卷可以比 Pod 的存活期长。 当 Pod 不再存在时，Kubernetes 也会销毁临时卷；不过 Kubernetes 不会销毁 持久卷。对于给定 Pod 中任何类型的卷，在容器重启期间数据都不会丢失。

​		有如下方式类型可以进行持久化（不全）：

|        持久化类型         |                             描述                             |                           官网示例                           |
| :-----------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| **awsElasticBlockStore**  | `awsElasticBlockStore` 卷将 Amazon Web服务（AWS）[EBS 卷](https://aws.amazon.com/ebs/) 挂载到你的 Pod 中。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#aws-ebs-%E9%85%8D%E7%BD%AE%E7%A4%BA%E4%BE%8B) |
|       **azureDisk**       | `azureDisk` 卷类型用来在 Pod 上挂载 Microsoft Azure [数据盘（Data Disk）](https://azure.microsoft.com/en-us/documentation/articles/virtual-machines-linux-about-disks-vhds/) 。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#azuredisk) |
|        **cephfs**         |      `cephfs` 卷允许你将现存的 CephFS 卷挂载到 Pod 中。      | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#cephfs) |
|        **cinder**         |   `cinder` 卷类型用于将 OpenStack Cinder 卷挂载到 Pod 中。   | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#cinder) |
|       **configMap**       | ConfigMap 对象中存储的数据可以被卷引用，然后被 Pod 中运行的 容器化应用使用。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#configmap) |
|      **downwardAPI**      |   `downwardAPI` 卷用于使 downward API 数据对应用程序可用。   | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#downwardapi) |
|       **emptyDir**        | 当 Pod 分派到某个 Node 上时，`emptyDir` 卷会被创建。<br />并且在 Pod 在该节点上运行期间，卷一直存在。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#emptydir) |
|     **fc (光纤通道)**     |    `fc` 卷类型允许将现有的光纤通道块存储卷挂载到 Pod 中。    | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#fc) |
|   **gcePersistentDisk**   | `gcePersistentDisk` 卷能将谷歌计算引擎 (GCE) [持久盘（PD）](http://cloud.google.com/compute/docs/disks) 挂载到你的 Pod 中。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#gcepersistentdisk) |
|       **glusterfs**       | `glusterfs` 卷能将 [Glusterfs](https://www.gluster.org/) (一个开源的网络文件系统) 挂载到你的 Pod 中。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#glusterfs) |
|       **hostPath**        | `hostPath` 卷能将主机节点文件系统上的文件或目录挂载到你的 Pod 中。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#hostpath) |
|         **iscsi**         | `iscsi` 卷能将 iSCSI (基于 IP 的 SCSI) 卷挂载到你的 Pod 中。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#iscsi) |
|         **local**         | `local` 卷所代表的是某个被挂载的本地存储设备，例如磁盘、分区或者目录。<br />`local` 卷只能用作静态创建的持久卷。尚不支持动态配置。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#local) |
|          **nfs**          |     `nfs` 卷能将 NFS (网络文件系统) 挂载到你的 Pod 中。      | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#nfs) |
| **persistentVolumeClaim** | `persistentVolumeClaim` 卷用来将[持久卷](https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/)（PersistentVolume） 挂载到 Pod 中。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#persistentvolumeclaim) |
|    **portworxVolume**     | `portworxVolume` 是一个可伸缩的块存储层<br />能够以超融合（hyperconverged）的方式与 Kubernetes 一起运行。 | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#portworxvolume) |
|     **vsphereVolume**     |  `vsphereVolume` 用来将 vSphere VMDK 卷挂载到你的 Pod 中。   | [点击进入](https://kubernetes.io/zh/docs/concepts/storage/volumes/#vspherevolume) |



### persistentVolumeClaim

​		存储的管理是一个与计算实例的管理完全不同的问题。PersistentVolume 子系统为用户 和管理员提供了一组 API，将存储如何供应的细节从其如何被使用中抽象出来。 

​		为了实现这点，我们引入了两个新的 API 资源：PersistentVolume 和 PersistentVolumeClaim。

​		**持久卷**（**PersistentVolume**，PV）是集群中的一块存储，可以由管理员事先供应，或者 使用[存储类（Storage Class）](https://kubernetes.io/zh/docs/concepts/storage/storage-classes/)来动态供应。 持久卷是集群资源，就像节点也是集群资源一样。PV 持久卷和普通的 Volume 一样，也是使用 卷插件来实现的，只是它们拥有独立于任何使用 PV 的 Pod 的生命周期。 此 API 对象中记述了存储的实现细节，无论其背后是 NFS、iSCSI 还是特定于云平台的存储系统。

​		**持久卷申领**（**PersistentVolumeClaim**，PVC）表达的是用户对存储的请求。概念上与 Pod 类似。 Pod 会耗用节点资源，而 PVC 申领会耗用 PV 资源。Pod 可以请求特定数量的资源（CPU 和内存）；同样 PVC 申领也可以请求特定的大小和访问模式 （例如，可以要求 PV 卷能够以 ReadWriteOnce、ReadOnlyMany 或 ReadWriteMany 模式之一来挂载，参见[访问模式](https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#access-modes)）。

​		尽管 PersistentVolumeClaim 允许用户消耗抽象的存储资源，常见的情况是针对不同的 问题用户需要的是具有不同属性（如，性能）的 PersistentVolume 卷。 集群管理员需要能够提供不同性质的 PersistentVolume，并且这些 PV 卷之间的差别不 仅限于卷大小和访问模式，同时又不能将卷是如何实现的这些细节暴露给用户。 为了满足这类需求，就有了 *存储类（StorageClass）* 资源。

​		PV 卷的供应有两种方式：静态供应或动态供应。

​				**静态**：集群管理员创建若干 PV 卷。这些卷对象带有真实存储的细节信息，并且对集群 用户可用（可见）。PV 卷对象存在于 Kubernetes API 中，可供用户消费（使用）。

​				**动态**：如果管理员所创建的所有静态 PV 卷都无法与用户的 PersistentVolumeClaim 匹配， 集群可以尝试为该 PVC 申领动态供应一个存储卷。 这一供应操作是基于 StorageClass 来实现的：PVC 申领必须请求某个 [存储类](https://kubernetes.io/zh/docs/concepts/storage/storage-classes/)，同时集群管理员必须 已经创建并配置了该类，这样动态供应卷的动作才会发生。 如果 PVC 申领指定存储类为 `""`，则相当于为自身禁止使用动态供应的卷。

​		简单的概述就是我们要创建**PersistentVolume（持久卷）**然后通过**PersistentVolumeClaim（持久卷申领）**来进行绑定，如果**PersistentVolumeClaim（持久卷申领）**绑定到了**PersistentVolume（持久卷）**那么则属于静态，如果没有绑定但是指定了**存储类（Storage Class）**那么则会自动创建，绑定规则如下:

​				用户创建一个带有特定存储容量和特定访问模式需求的 PersistentVolumeClaim 对象； 在动态供应场景下，这个 PVC 对象可能已经创建完毕。 主控节点中的控制回路监测新的 PVC 对象，寻找与之匹配的 PV 卷（如果可能的话）， 并将二者绑定到一起。 如果为了新的 PVC 申领动态供应了 PV 卷，则控制回路总是将该 PV 卷绑定到这一 PVC 申领。 否则，用户总是能够获得他们所请求的资源，只是所获得的 PV 卷可能会超出所请求的配置。 一旦绑定关系建立，则 PersistentVolumeClaim 绑定就是排他性的，无论该 PVC 申领是 如何与 PV 卷建立的绑定关系。 PVC 申领与 PV 卷之间的绑定是一种一对一的映射，实现上使用 ClaimRef 来记述 PV 卷 与 PVC 申领间的双向绑定关系。

​				如果找不到匹配的 PV 卷，PVC 申领会无限期地处于未绑定状态。 当与之匹配的 PV 卷可用时，PVC 申领会被绑定。 例如，即使某集群上供应了很多 50 Gi 大小的 PV 卷，也无法与请求 100 Gi 大小的存储的 PVC 匹配。当新的 100 Gi PV 卷被加入到集群时，该 PVC 才有可能被绑定。



```

```



​		配置：[点击进入](https://kubernetes.io/zh/docs/tasks/configure-pod-container/configure-persistent-volume-storage/)

​		创建PersistentVolume卷

```sh
# 设置创建的目录卷
export pvPath="/root/k8s/test/pv"
mkdir -p $pvPath && cd $pvPath

# 创建一个本地Local的卷,存储为10个G
cat > test-local-pv.yaml << EOF
apiVersion: v1
kind: PersistentVolume
metadata:
  name: test-pv-volume
spec:
  storageClassName: test-local
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: "/mnt/testLocal"
EOF

# 创建PV
kubectl apply -f test-local-pv.yaml

# 查看PV
kubectl get pv

# NAME             CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM   STORAGECLASS   REASON   AGE
# test-pv-volume   10Gi       RWO            Retain           Available           test-local              107s

# 状态Available未绑定PersistentVolumeClaim
```



# ConfigMap

​		POD中配置ConfigMap官网地址：[点击进入](https://kubernetes.io/zh/docs/concepts/configuration/configmap/)

## 基于目录以及文件

```sh
# 新建目录
export configMapFilePath="/root/k8s/test/configMap/testPath"
export configMapNameSpace="default"

mkdir -p $configMapFilePath && cd $configMapFilePath

# 新建配置文件
cat > $configMapFilePath/mysql << EOF
mysql.username=root
mysql.password=root
mysql.port=3306
EOF
cat > $configMapFilePath/mongo << EOF
mongo.username=mongo
mongo.password=mongoroot
mongo.port=27017
EOF

# 创建ConfigMap
kubectl create configmap mysql-mongo-config --from-file=$configMapFilePath -n $configMapNameSpace

# 查看configMap
kubectl describe configmaps mysql-mongo-config


#   Data
#   ====
#   mongo:
#   ----
#   mongo.username=mongo
#   mongo.password=mongoroot
#   mongo.port=27017

#   mysql:
#   ----
#   mysql.username=root
#   mysql.password=root
#   mysql.port=3306

# kubectl get configmaps mysql-mongo-config -o yaml

# 基于单个OR多个文件创建
# 单个
kubectl create configmap mysql-mongo-config-1 --from-file=$configMapFilePath/mysql
# 多个
kubectl create configmap mysql-mongo-config-2 --from-file=$configMapFilePath/mysql --from-file=$configMapFilePath/mongo




# ！！！清理环境
# 删除ConfigMap
kubectl delete configmap mysql-mongo-config -n $configMapNameSpace
kubectl delete configmap mysql-mongo-config
kubectl delete configmap mysql-mongo-config-1
kubectl delete configmap mysql-mongo-config-2
```

## 基于Yaml

​		创建一个ConfigMap

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysql-mongo-config
  namespace: default
# 配置数据
data:
	# 配置项
  mysql.db.name: "nacos_devtest"
  mysql.port: "3306"
  mysql.user: "nacos"
  mysql.password: "nacos"
  
  # 配置项
	demo.username: "bigkang"  
	demo.password: "ad31?dad.>dw%32"
	
	# 多文件配置项
  mysql: |
    mysql.username=root
    mysql.password=root
    mysql.port=3306
  mongo: |
    mongo.username=mongo
    mongo.password=mongoroot
    mongo.port=27017
```

## POD中使用

​		将单个配置引入

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: dapi-test-pod
spec:
  containers:
    - name: test-container
      image: k8s.gcr.io/busybox
      command: [ "/bin/sh", "-c", "echo $(MYSQL_USERNAME)" ] # 数据环境变量
      env:
        - name: MYSQL_USERNAME
          valueFrom:
            configMapKeyRef:
              name: mysql-mongo-config 	# 使用的configMap
              key: mysql.user 					# 使用的键
        - name: LOG_LEVEL
          valueFrom:
            configMapKeyRef:
              name: mysql-mongo-config	# 使用的configMap
              key: demo.username				# 使用的键
```

​		直接引入整个配置文件（引入整合配置文件进行覆盖，将所有的键环境变量都覆盖进来）

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: dapi-test-pod
spec:
  containers:
    - name: test-container
      image: k8s.gcr.io/busybox
      command: [ "/bin/sh", "-c", "env" ]
      envFrom:
      - configMapRef:
          name: mysql-mongo-config
  restartPolicy: Never
```

​		volumes数据卷使用ConfigMap

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: dapi-test-pod
spec:
  containers:
    - name: test-container
      image: k8s.gcr.io/busybox
      command: [ "/bin/sh", "-c", "ls /etc/config/" ]
      volumeMounts:
      - name: config-volume
        mountPath: /etc/config
  volumes:
    - name: config-volume
      configMap:
        # Provide the name of the ConfigMap containing the files you want
        # to add to the container
        name: special-config
  restartPolicy: Never
```

​	



# Service



# Secret

​		官网地址：[点击进入](https://kubernetes.io/zh/docs/concepts/configuration/secret/)

​		

# KubeCtl

​		获取当前k8s节点

```sh
# 获取所有节点
kubectl get nodes

# 获取带yunyao1这个节点
kubectl get nodes yunyao1

# 获取节点详细信息
kubectl get nodes -o wide
	-o支持如下
			custom-columns
			custom-columns-file
			go-template
			go-template-file
			json
			jsonpath
			jsonpath-file
			name
			template
			templatefile
			wide
			yaml
```



​		查看端口

```
kubectl get svc
```

​		

​		获取pods

```sh
kubectl get pods

# 获取所有pods所有命名空间下的
kubectl  get pods -A

```

​		获取健康信息

```
kubectl get cs
```





​		查看当前的K8s版本

```
 kubectl version
```



# 集群管理

## 查看集群信息

查看当前集群的节点，以及节点信息

```
kubectl get nodes -o wide
```



# 资源控制

## pod

​		[负载](https://kubernetes.io/zh/docs/concepts/workloads/)资源的控制器通常使用 *Pod 模板（Pod Template）* 来替你创建 Pod 并管理它们。Pod 模板是包含在工作负载对象中的规范，用来创建 Pod。这类负载资源包括 [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/)、 [Job](https://kubernetes.io/zh/docs/concepts/workloads/controllers/job/) 和 [DaemonSets](https://kubernetes.io/zh/docs/concepts/workloads/controllers/daemonset/)等。工作负载的控制器会使用负载对象中的 `PodTemplate` 来生成实际的 Pod。 `PodTemplate` 是你用来运行应用时指定的负载资源的目标状态的一部分。

​		使用Job运行一个pod

```

```

