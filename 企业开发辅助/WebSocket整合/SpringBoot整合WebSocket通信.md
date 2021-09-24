# 引入依赖 

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
				<dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```

# 编写代码

### 编写服务类

```java
/**
 * @Author BigKang
 * @Date 2019/10/21 3:26 PM
 * @Summarize WebSocket服务
 */
@ServerEndpoint(value = "/websocket/client")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * Juc原子Integer（线程安全Integer）
     */
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);

    /**
     * CopyOnWrite写时复制Set，用于存放Session信息（线程安全）
     */
    private static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<Session>();


    /**
     * 初始化WebSocket调用方法
     */
    @PostConstruct
    public void init() {
        log.info("Init WebSocket,初始化WebSocket服务......");
    }

    /**
     * 连接建立成功调用方法
     */
    @OnOpen
    public void onOpen(Session session) {
        SessionSet.add(session);
        // 连接数+1
        int cnt = OnlineCount.incrementAndGet();
        log.info("连接加入，当前连接数：{}", cnt);
        SendMessage(session, "连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        SessionSet.remove(session);
        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     * 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到消息：{}",message);
//        SendMessage(session, message);
    }

    /**
     * 出现错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}",error.getMessage(),session.getId());
        error.printStackTrace();
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     * @param session
     * @param message
     */
    public static void SendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     * @param message
     * @throws IOException
     */
    public static void BroadCastInfo(String message) {
        for (Session session : SessionSet) {
            if(session.isOpen()){
                SendMessage(session, message);
            }
        }
    }

    /**
     * 指定Session发送消息
     * @param sessionId
     * @param message
     * @throws IOException
     */
    public static void SendMessage(String message,String sessionId) throws IOException {
        Session session = null;
        for (Session s : SessionSet) {
            if(s.getId().equals(sessionId)){
                session = s;
                break;
            }
        }
        if(session!=null){
            SendMessage(session, message);
        }
        else{
            log.warn("没有找到你指定ID的会话：{}",sessionId);
        }
    }

    /**
     * 返回当前连接数量
     * @return
     * @throws IOException
     */
    public static Integer getOnLineCount() {
        return OnlineCount.get();
    }

}

```

### 编写配置类

```java
/**
 * @Author BigKang
 * @Date 2019/10/21 3:27 PM
 * @Summarize WebSocket配置类
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter () {
        return new ServerEndpointExporter();
    }

}
```

### 编写控制器

```java
@RestController
@RequestMapping("websocket")
@Api(tags = "WebSocket测试控制器")
public class WebSocketController {

    /**
     * 群发消息内容
     * @param message
     * @return
     */
    @ApiOperation("群发广播消息")
    @GetMapping(value = "/sendAll")
    public String sendAllMessage(@RequestParam(required = true) String message) {
        WebSocketServer.BroadCastInfo(message);
        return "ok";
    }

    /**
     * 指定会话ID发消息
     * @param message 消息内容
     * @param id      连接会话ID
     * @return
     */
    @ApiOperation("指定sessionId发送消息")
    @RequestMapping(value = "/sendOne")
    public String sendOneMessage(@RequestParam String message, @RequestParam String id) {
        try {
            WebSocketServer.SendMessage(message, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @ApiOperation("获取当前连接数量")
    @GetMapping("getOnLineCount")
    public Integer getOnLineCount(){
        return WebSocketServer.getOnLineCount();
    }
}
```

### 编写前端页面

新建一个html文件，然后保存。打开html页面，查看console控制台进行测试

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta http-equiv="Content-Style-Type" content="text/css">
  <title></title>
  <meta name="Generator" content="Cocoa HTML Writer">
  <meta name="CocoaVersion" content="1671.2">
  <style type="text/css">
  </style>
</head>
<body>

<h3>WebSocket测试，打开Console控制台查看测试信息！</h3>
<h1>
    <input type="text" id="sendtxt" /> <input type="button" value="发送消息" onclick="sendMsg()" /> 
</h1>

<script type="text/javascript">
    var socket;
    if (typeof (WebSocket) == "undefined") {
        console.log("浏览器不支持WebSocket");
    } else {
        console.log("浏览器支持WebSocket");

        //实现化WebSocket对象
        //指定要连接的服务器地址与端口建立连接
        //注意ws、wss使用不同的端口。我使用自签名的证书测试，
        //无法使用wss，浏览器打开WebSocket时报错
        //ws对应http、wss对应https。
        socket = new WebSocket("ws://localhost:8080/websocket/client");
        //连接打开事件
        socket.onopen = function() {
            console.log("开始建立连接");
            socket.send("请求建立连接");
        };
        //收到消息事件
        socket.onmessage = function(msg) {
            alert(msg.data);
        };
        //连接关闭事件
        socket.onclose = function() {
            console.log("连接已关闭");
        };
        //发生了错误事件
        socket.onerror = function() {
            alert("连接发生错误");
        }

        //窗口关闭时，关闭连接
        window.unload=function() {
            socket.close();
        };
    }

    function sendMsg(){
    	var msg = document.getElementById("sendtxt").value;
    	socket.send(msg);
    }
</script>

</body>
</html>

```

