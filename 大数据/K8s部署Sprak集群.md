# 准备

## 命名空间

```Bash
kubectl create namespace spark-cluster
```

## PVC

```Bash
# 创建部署脚本
echo "apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: spark-data-pvc
  namespace: spark-cluster
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 100Gi
  storageClassName: csi-s3" > sprak-pv.yaml
  
  # 应用
  kubectl  apply -f sprak-pv.yaml
```

# 服务部署

## master

```Bash
# 创建部署脚本
echo "
apiVersion: v1
kind: Service
metadata:
  name: spark-master
  namespace: spark-cluster
spec:
  ports:
    - port: 7077
      targetPort: 7077
      name: master
    - port: 8080
      targetPort: 8080
      name: ui
  selector:
    app: spark-master
  clusterIP: None
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: spark-master
  namespace: spark-cluster
spec:
  serviceName: spark-master
  replicas: 1
  selector:
    matchLabels:
      app: spark-master
  template:
    metadata:
      labels:
        app: spark-master
    spec:
      containers:
      - name: spark-master
        image: registry.cn-beijing.aliyuncs.com/honglingcr/spark:3.5.2-scala2.12-java17-python3-r-ubuntu
        command: ["/opt/spark/bin/spark-class", "org.apache.spark.deploy.master.Master"]
        args: ["--host", "spark-master-0.spark-master.spark-cluster.svc.cluster.local"]
        env:
          - name: TZ
            value: "Asia/Shanghai"
        volumeMounts:
          - name: spark-data
            mountPath: /opt/spark/data
        ports:
          - containerPort: 7077
            name: master
          - containerPort: 8080
            name: ui
      volumes:
        - name: spark-data
          persistentVolumeClaim:
            claimName: spark-data-pvc
" > sprak-master-server.yaml


  # 应用
  kubectl  apply -f sprak-master-server.yaml
```

## node

```Bash
# 创建部署脚本
echo "
apiVersion: v1
kind: Service
metadata:
  name: spark-worker
  namespace: spark-cluster
spec:
  ports:
    - port: 8081
      targetPort: 8081
      name: ui
  selector:
    app: spark-worker
  clusterIP: None
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: spark-worker
  namespace: spark-cluster
spec:
  replicas: 2  # 可以根据需要增加副本数
  selector:
    matchLabels:
      app: spark-worker
  template:
    metadata:
      labels:
        app: spark-worker
    spec:
      containers:
      - name: spark-worker
        image: registry.cn-beijing.aliyuncs.com/honglingcr/spark:3.5.2-scala2.12-java17-python3-r-ubuntu
        command: ["/opt/spark/bin/spark-class", "org.apache.spark.deploy.worker.Worker"]
        args: ["spark://spark-master-0.spark-master.spark-cluster.svc.cluster.local:7077"]
        env:
          - name: TZ
            value: "Asia/Shanghai"
        volumeMounts:
          - name: spark-data
            mountPath: /opt/spark/data
        ports:
          - containerPort: 8081
            name: ui
      volumes:
        - name: spark-data
          persistentVolumeClaim:
            claimName: spark-data-pvc
" > sprak-node-server.yaml

  # 应用
  kubectl  apply -f sprak-node-server.yaml
```

## nodeport

```Bash
# 创建部署脚本
echo "
apiVersion: v1
kind: Service
metadata:
  name: spark-master-nodeport
  namespace: spark-cluster
  labels:
    app: spark-master
spec:
  type: NodePort
  selector:
    app: spark-master
  ports:
    - port: 7077
      targetPort: 7077
      nodePort: 31077
      name: master
    - port: 8080
      targetPort: 8080
      nodePort: 31078
      name: ui
" > sprak-master-nodeport.yaml

# 应用
  kubectl  apply -f sprak-master-nodeport.yaml
```

# 测试

```Bash
./spark-submit --master spark://192.168.2.200:31077 --class org.apache.spark.examples.SparkPi /opt/spark/examples/jars/spark-examples_2.12-3.5.2.jar 100
```