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



# POD

​		POD是通过资源从模板中进行创建并且管理的，PodTemplates是用于创建Pod的规范，并且包含在工作负载资源，工作负载资源又分为3种，分别是如下3种：

- ​						[Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)

- ​						[Jobs](https://kubernetes.io/docs/concepts/workloads/controllers/job/)

- ​						[DaemonSet](https://kubernetes.io/docs/concepts/workloads/controllers/daemonset/)

​		下面我们将通过如下三种方式创建POD

## Deployment

### 简介

​		官网地址：[点击进行](https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#deployment-v1-apps)

​		一个部署提供了声明更新[POD](https://kubernetes.io/docs/concepts/workloads/pods/) 和 [副本集](https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/)，您在部署中描述*所需的状态*，然后在部署中[控制者](https://kubernetes.io/docs/concepts/architecture/controller/)以受控速率将实际状态更改为所需状态。您可以定义部署以创建新的副本集，或删除现有部署并在新部署中采用其所有资源。

​		分别有五个根字段

- ​				apiVersion

```
		APIVersion定义了该对象表示形式的版本控制架构。服务器应将已识别的架构转换为最新的内部值，并可能拒绝无法识别的值。更多信息：https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#resources
```

- ​				kind

```
		Kind是一个字符串值，表示此对象表示的REST资源。服务器可以从客户端向其提交请求的端点推断出这一点。无法更新。在CamelCase中。更多信息：https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds
```

- ​				[metadata](https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#objectmeta-v1-meta)

```
		对象元	标准对象元数据。
```

- ​				[spec](https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#deploymentspec-v1-apps)

```
		指定部署所需的行为。		
```

- ​				[status](https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#deploymentstatus-v1-apps)

```
		最近观察到的部署状态。
```

​		例如我们查看官网的示例：

```yaml
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



## Jobs



## DaemonSets

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

