

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

