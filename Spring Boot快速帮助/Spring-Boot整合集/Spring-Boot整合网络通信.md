# UDP

​         UDP在Socket网络中都是通过DatagramPacket的数据格式进行传输的，也就是数据报的形式。UDP数据报是基于IP数据报建立的，UDP数据报同样分为首部和主体，UDP数据报的首部比IP数据报的首部多了8个字节，其中UDP的首部包括：**源端口号和目标端口号、** **IP首部之后所有内容的长度、** **可选的检验和。** 理论上UDP包中的数据长度最大是65507字节，但实际上总是比这少得多

## DatagramPacket（数据报）

### 接收数据报造函数简介

接收数据报的构造函数，这两个构造函数可以创建新的DatagramPacket对象并从网络接收数据。

```java
public DatagramPacket(byte buf[], int length)
//当Socket接收一个数据报时，它将数据报的数据部分存储在buf字节数组中，从buf[0]开始一直到包完全存储，或者直到向字节数组中写入了length个字节为止。
 示例：
 	byte[] buf = new byte[1024];
	DatagramPacket packet = new DatagramPacket(buf, buf.length);

public DatagramPacket(byte buf[], int offset, int length)
//将从buf[offset]开始存储，直到包完全存储，或者直到向字节数组中写入了length个字节为止。
 示例：
    byte[] buf = new byte[1024];
	DatagramPacket packet = new DatagramPacket(buf, 0, buf.length);
```

### 发送数据报造函数简介

```java
public DatagramPacket(byte buf[], int length, SocketAddress address)
//buf发送的数据字节，length从0到多少字节的数据，address发送的目标的主机地址
  示例如下：
        try {
            String str = "Hello World";
            byte[] bytes = str.getBytes("UTF-8");
			SocketAddress address = new InetSocketAddress("127.0.0.1",6007);
            // 构造DatagramPacket
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
            // 发送数据
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

```

```java
public DatagramPacket(byte buf[], int offset, int length, SocketAddress address)
//buf发送的数据字节，offset从哪个字节开始，offset开始向后多少字节，address发送的目标的主机地址
  示例如下：
        try {
            String str = "Hello World";
            byte[] bytes = str.getBytes("UTF-8");
			SocketAddress address = new InetSocketAddress("127.0.0.1",6007);
            int offset = 2;
            // 构造DatagramPacket
            DatagramPacket packet = new DatagramPacket(bytes, offset,bytes.length-offset, address);
            // 发送数据
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

```

```java
public DatagramPacket(byte buf[], int length,InetAddress address, int port)
//buf发送的数据字节，length从0到多少字节的数据，address发送的目标的主机地址,port端口号
  示例如下：
        try {
            String str = "Hello World";
            byte[] bytes = str.getBytes("UTF-8");
            InetAddress address = InetAddress.getByName("127.0.0.1");
            int port = 8081;
            // 构造DatagramPacket
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address,port);
            // 发送数据
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
```

```java
public DatagramPacket(byte buf[], int offset, int length,InetAddress address, int port)
//buf发送的数据字节，offset从offset到多少的长度或结尾的字节，从offset开始后多少字节，address发送的目标的主机地址,port端口号
  示例如下：
        try {
            String str = "Hello World";
            byte[] bytes = str.getBytes("UTF-8");
            InetAddress address = InetAddress.getByName("127.0.0.1");
            int port = 8081;
            int offset = 10;
            // 构造DatagramPacket
            DatagramPacket packet = new DatagramPacket(bytes, offset,bytes.length-offset, address,port);
            // 发送数据
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
```

### 常用方法

#### get方法

```java
getAddress()						
	返回一个InetAddress对象，指示远程主机的地址。这里有两种情况：
		一是如果数据报是从Internet接收的，返回的地址则是发送该数据报的机器的地址（源地址）；
		二是如果数据报是本地创建准备发送到远程主机的，返回的地址则是要发往的远程主机的地址（目标地址）。
		  这个方法常用于确定发送UDP数据报的主机地址，使接收方可以回复。
		  
getPort()
	返回一个int整数，指示远程端口号。这里有两种情况：
		一是如果数据报是从Internet接收的，返回的端口号则是发送该数据报的机器的端口号（源端口号）；
		二是如果数据报是本地创建准备发送到远程主机的，返回的端口号则是要发往的远程主机的端口号（目标端口号）。
		
getSocketAddress()
	返回一个SocketAddress对象，指示远程主机的IP地址和端口号。和上面类似
	
getData()
	返回一个byte数组，指示数据报中的数据部分。通常必须将接收到的字节数组转化为其他的某种数据形式以便使用。

getLength()
	返回一个int整数，指示数据报中数据部分的字节数。它不一定等于getData()返回的数组长度（即getData().length），甚至可能小于。

getOffset()
	返回一个int整数，指示该字节数组中的一个位置，即开始填充数据报的位置。
```

#### set方法

```java
setData(byte[] buf)
    该方法会改变数据报的数据部分。如果要向远程主机发送大文件可能会用到这个方法。
setData(byte[] buf, int offset, int length)
    该重载方法提供另一个途径来发送大量的数据。与发送大量新数组不同，可以将所有数据放在一个数据中，每次发送一部分。
setAddress(InetAddress iaddr)
    该方法会修改数据报发往的地址。允许将同一个数据报发送给多个不同的接收方。
setPort(int iport)
 	该方法会修改数据报发往的端口。
setSocketAddress(SocketAddress address)
    该方法会修改数据报要发往的地址和端口。在响应回复时可以使用这个方法。
setLength(int length)
    该方法会修改内部缓冲区中包含实际数据报数据的字节数，而不包括未填充数据的空间。这个方法在接收数据报时很有用，当接收到数据报时，其长度设置为入站数据的长度。
```

## DatagramSocket（连接）

​		要发送和接受数据报DatagramPacket，必须通过DatagramSocket进行传输，所有DatagramSocket都绑定到一个本地端口，在这个端口上监听入站数据，**这个端口也会放置到出站数据报的首部中**，以便服务器用来响应数据报的发送地址。一般情况下，客户端使用匿名端口，服务器需要指定监听端口。

### 构造函数

```java
public DatagramSocket() throws SocketException
	该构造函数创建一个绑定到匿名端口的Socket，系统会自动为客户端分配一个端口。
	
public DatagramSocket(int port) throws SocketException
	该构造函数创建一个指定端口的Socket，可以使用这个方法编写在已知端口监听的服务器。

public DatagramSocket(int port, InetAddress laddr) throws SocketException
	该构造函数创建在指定端口和网络接口的Socket，多用于多宿主主机（一个主机有多个IP地址）。第二个参数是匹配该主机某个网络接口的InetAddress对象。
	
public DatagramSocket(SocketAddress bindaddr) throws SocketException
	该构造函数与第3个相似，只是网络接口地址和端口由SocketAddress包装。
	
protected DatagramSocket(DatagramSocketImpl impl)
    该构造函数允许子类提供自己的UDP实现，而不接受默认实现。与其他四个方法不同，这个Socket一开始没有与端口绑定，使用前必须通过bind()方法绑定到一个SocketAddress。
```

### 发送和接受数据报

示例参考DatagramPacket数据报

```java
public void send(DatagramPacket p) throws IOException
	发送该数据报
	
public synchronized void receive(DatagramPacket p) throws IOException
	如果没有数据报则一直等待

public void close()
    关闭连接
    
public int getLocalPort()
    返回一个int整数，表示Socket正在监听的本地端口。如果创建了一个匿名端口的DatagramSocket，可以使用该方法获取到监听的端口号。
    
public InetAddress getLocalAddress()
    返回一个InetAddress对象，表示Socket绑定到的本地地址。实际中很少这样做，因为你已经知道了监听的地址。
```

### 管理连接

```java
public void connect(InetAddress address, int port)
    并不真正建立TCP意义上的连接，不过它确实指定了DatagramSocket只对指定远程主机和指定远程端口发送和接受数据报。试图向其它主机和端口发送数据报将抛出异常，而从其它主机和端口接受的数据报将直接丢弃，没有异常也没有通知。
    示例：
    		DatagramSocket socket = new DatagramSocket(8881);
    		socket.connect(InetAddress.getByName("127.0.0.1"),6007);
public void connect(SocketAddress addr) throws SocketException
	类似如上示例
	
public void disconnect()
    中断连接，从而可以再次收发其它主机和端口的数据报。
    
public int getPort()
    当且仅当DatagramSocket已连接时，该方法返回所连接的远程端口，否则返回-1。
    
public InetAddress getInetAddress()
    当且仅当DatagramSocket已连接时，该方法返回所连接的远程主机地址，否则返回null。
    
public SocketAddress getRemoteSocketAddress()
    如果DatagramSocket已连接，该方法返回所连接的远程主机的地址，否则返回null。
```

### 设置参数

```java
public synchronized void setSoTimeout(int timeout) throws SocketException
	该属性是receive()方法在抛出InterruptedException异常前等待接收数据报的时间。如果该属性值为0，则receive()方法永远不会超时。如果要实现一个安全协议，需要在一定时间内响应就可能需要设置该属性。setSoTimeout()方法可以设置超时时间，如果超时了，阻塞的receive()方法就会抛出SocketTimeoutException异常。必须在receive()方法前调用。
	
public synchronized void setReceiveBufferSize(int size) throws SocketException
	对于相当快的连接（如以太网的连接），较大的缓冲区有助于提升性能，因为在溢出前可以存储更多的入站数据报。与TCP相比，对于UDP而言，足够大的接收缓冲区甚至更为重要，因为在缓冲区满时到达的UDP数据报会丢失，而缓冲区满时到达的TCP数据报最后还会重传。
	
public synchronized void setSendBufferSize(int size) throws SocketException
	设置网络输出的发送缓冲区大小。
	
public synchronized void setReuseAddress(boolean on) throws SocketException
	对于UDP来说，该属性可以控制是否允许多个数据报Socket同时绑定到相同的端口和地址。如果多个Socket绑定到相同端口，接收的包将复制给绑定的所有Socket。必须在新Socket绑定到端口前调用setReuseAddress()。
	
public synchronized void setBroadcast(boolean on) throws SocketException
	该属性控制是否允许一个Socket向广播地址收发数据报。默认为true。
```



此文章根据Samuel_Tom作品改编
原文链接：https://www.jianshu.com/p/54cfa3ce0edf
来源：简书

来源：简书

简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。