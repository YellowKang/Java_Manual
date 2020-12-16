# 为什么需要远程登录执行？

​		我们有时候通过业务代码会关联一些东西，那么在这个时候做完操作后有可能需要去其他服务器上执行一些命令，例如我们更换了什么文件，然后需要重启另一个服务，那么这个时候就需要我们去远程执行命令了。

# 如何远程执行命令？

​		有两种方式，我们可以使用jsch和ganymed来进行实现，两个包都是对SSH2的封装，能够帮助我们远程连接服务器，并且执行命令。



# jsch

​		引入依赖

```xml
<!-- https://mvnrepository.com/artifact/com.jcraft/jsch -->
<dependency>
    <groupId>com.jcraft</groupId>
    <artifactId>jsch</artifactId>
    <version>0.1.55</version>
</dependency>

```

## 远程执行命令

​		首先新建工具类

```java
package com.test.boot.utils;

import com.jcraft.jsch.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author BigKang
 * @Date 2020/12/14 5:45 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize Jsch工具类
 */
@Slf4j
@Getter
@Setter
public class JschUtil {

    /**
     * 主机IP
     */
    private String host;

    /**
     * 默认端口
     */
    private int port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 设置编码格式
     */
    private String charset;

    /**
     * JSch对象
     */
    private JSch jsch;

    /**
     * 会话Session
     */
    private Session session;


    /**
     * 默认端口号
     */
    private static final Integer DEFAULT_PORT = 22;
    /**
     * 默认编码
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 构造方法
     */
    public JschUtil() {
    }

    /**
     * 构造方法
     *
     * @param host     HostIp地址
     * @param port     端口号
     * @param username 用户名
     * @param password 密码
     * @param charset  编码
     */
    public JschUtil(String host, int port, String username, String password, String charset) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.charset = charset;
    }

    /**
     * 构造方法
     *
     * @param host     HostIp地址
     * @param port     端口号
     * @param username 用户名
     * @param password 密码
     */
    public JschUtil(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.charset = DEFAULT_CHARSET;
    }

    /**
     * 构造方法
     *
     * @param host     HostIp地址
     * @param username 用户名
     * @param password 密码
     */
    public JschUtil(String host, String username, String password) {
        this.host = host;
        this.port = DEFAULT_PORT;
        this.username = username;
        this.password = password;
        this.charset = DEFAULT_CHARSET;
    }

    /**
     * 连接到指定的IP
     *
     * @throws JSchException
     */
    private void connect() {
        // 连接到SSH服务器
        try {
            jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(10000);
            session.connect();
            log.debug("SSH2 Client:{} Success", host);
        } catch (Exception e) {
            throw new RuntimeException("连接SSH失败!");
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        // 关闭Session会话，SFTP使用同一个Session会随之关闭
        session.disconnect();
    }


    /**
     * 执行Command命令
     * @param command 命令字符串
     * @return
     */
    public String execCommand(String command) {
        connect();
        ChannelExec exec = null;
        InputStream in = null;
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();
        try {
            exec = (ChannelExec) session.openChannel("exec");
            exec.setCommand(command);
            exec.connect();
            in = exec.getInputStream();
            reader = new BufferedReader(
                    new InputStreamReader(in));
            String tmpStr = "";
            while ((tmpStr = reader.readLine()) != null) {
                result.append(new String(tmpStr.getBytes("gbk"), "UTF-8")).append("\n");
            }
        } catch (IOException | JSchException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                reader.close();
                in.close();
                exec.disconnect();
                close();
            }catch (Exception e){
                log.error("Close SSH Command Failure!");
            }

        }
        return result.toString();
    }

}

```

​		然后调用工具类即可

```java
        JschUtil jschUtil = new JschUtil("192.168.1.11",22,"root","root123");
        String s = jschUtil.execCommand("cd && ls");
        System.out.println(s);
```



## Java方式打造SSH客户端

```java
import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {
    private static Object object = new Object();

    public static void main(String[] args) throws IOException, JSchException, InterruptedException {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        // 设置用户名，地址，端口号
        Session session = jsch.getSession("bigkang", "39.108.158.33", 22);
        // 设置密码
        session.setPassword("Kangbaba666");
        session.setConfig(config);
        session.connect();
        // 设置管道类型为shell
        ChannelShell channel = (ChannelShell) session.openChannel("shell");
        channel.setPty(true);
        channel.connect();
        // 输入输出流
        InputStream inputStream = channel.getInputStream();
        OutputStream outputStream = channel.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        AtomicBoolean flag = new AtomicBoolean(true);
        new Thread(() -> {
            //如果没有数据来，线程会一直阻塞在这个地方等待数据。
            try {
                byte[] buffer = new byte[1024];
                int i = 0;
                //如果没有数据来，线程会一直阻塞在这个地方等待数据。
                while ((i = inputStream.read(buffer)) != -1) {
                    String str = new String(Arrays.copyOfRange(buffer, 0, i), "UTF-8");
                    System.out.print(str);
                }
                System.out.println("连接断开");
                flag.set(false);
                inputStream.close();
                outputStream.close();
                channel.disconnect();
                session.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "ReadThread").start();

        // 循环向输入流写入
        while (flag.get()) {
            String next = scanner.next();
            // 命令加上\r表示回车
            next += "\r";
            if(channel.getExitStatus() == -1) {
                // 写入
                outputStream.write(next.getBytes(), 0, next.length());
                outputStream.flush();
            }else {
                System.out.println("退出登录");
            }
        }
    }
```



# ganymed



​		一张图对接

​		两次聚类，排行话题

​		TOP10 今日报道

​		问清楚南区二级页功能（最高优先级）

​		事故灾害警报信息

​			事故最近48小时时间降序

​			时间修改为  月-日 时：分

​		会议纪要，需要出会议纪要报告

​		年底工作总结情况（工作内容，问题困难，未来工作）

​		煤矿，危险化学品等等

​		买数据入库等等

```

```

