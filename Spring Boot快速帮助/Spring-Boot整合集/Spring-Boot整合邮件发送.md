# 引入依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
```

# 配置文件

```properties
spring.mail.host=smtp.qq.com
spring.mail.username=bigkangsix@qq.com
#次密码为授权码，不是qq密码
spring.mail.password=qwe1221dwWQda

#启用验证
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.starttls.required=fasle
```

# 实现邮件发送

```java
	//创建一个Mail邮件发送器	
	@Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMail(){
    	//创建一个邮件消息
        MimeMessage message = null;
        try {
        	//给邮箱赋值一个发松器消息
            message = javaMailSender.createMimeMessage();
            
            //创建一个消息助手，由多部分组成（例如：消息加上附件，以及其他）
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
           	//发送方邮箱
            helper.setFrom("bigkangsix@qq.com");
            
            //接收方邮箱
            helper.setTo("13931657257m@sina.cn");
            
            //主题
            helper.setSubject("康哥专属测试邮件");


            StringBuffer sb = new StringBuffer();
		   //创建要发送的文本信息
            sb.append("<h1>大标题-h1</h1>")
                    .append("<p style='color:#F00'>红色字</p>")
                    .append("<p style='text-align:right'>右对齐</p>");
                    
            //设置文本，是否使用html编码
            helper.setText(sb.toString(), true);
            
            //创建一个附件，可以是文档或者图片等等
            FileSystemResource fileSystemResource=new FileSystemResource(new 		 
            File("E://tu.jpg"));

            //上传发送的的附件
            helper.addAttachment("电子发票",fileSystemResource);
            
            //发送信息
            javaMailSender.send(message);
            
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
```

​		这样我们就实现了简单的一个邮件的发送，我们还可以整合其他的技术然后来发送邮件

​		企业邮箱发送

```java
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();//直接生产一个实例
        mailSender.setHost("smtp.exmail.qq.com");
        mailSender.setPassword("qweasdzxc");
        mailSender.setPort(25);
        mailSender.setProtocol("smtp");
        mailSender.setUsername("bigkang@bjtopcom.com");
```

​		发送html+图片+附件

```java

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setPassword("afr123adaeqw");
        mailSender.setPort(25);
        mailSender.setProtocol("smtp");
        mailSender.setUsername("bigkangsix@qq.com");
        mailSender.setDefaultEncoding("UTF-8");
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("bigkangsix@qq.com");
        helper.setTo("446728012@qq.com");
        //主题
        helper.setSubject("测试邮件");


        StringBuffer sb = new StringBuffer();
        //创建要发送的文本信息
        sb.append("<h1>大标题-h1</h1>")
                .append("<p style='color:#F00'>红色字</p>")
                .append("<p style='text-align:right'>右对齐</p>")
                .append("<a href='https://baidu.com'>百度一下</a>")
                // 引入图片Cid
                .append("<img src='cid:test_img'/>");


        //设置文本，是否使用html编码
        helper.setText(sb.toString(),true);

        // 添加图片,图片添加顺序必须在html后面
        FileSystemResource file = new FileSystemResource(new File("/Users/bigkang/Documents/笔记/个人资源地址/bigkang.png"));
        helper.addInline("test_img", file);

        //创建一个附件，可以是文档或者图片等等
        FileSystemResource fileSystemResource = new FileSystemResource(new
                File("/Users/bigkang/Documents/笔记/个人资源地址/bigkang.png"));

        //上传发送的的附件
        helper.addAttachment("测试.png", fileSystemResource);

        //发送信息
        mailSender.send(mimeMessage);
```



# 不使用SpringBoot-Start发送

​		引入mail依赖

```xml
      <!-- https://mvnrepository.com/artifact/javax.mail/javax.mail-api -->
      <dependency>
          <groupId>javax.mail</groupId>
          <artifactId>javax.mail-api</artifactId>
          <version>1.6.2</version>
      </dependency>
```

​		连接以及发送消息

```java
   public void testemail() {

        Properties properties = new Properties();
        // 连接协议
        properties.put("mail.transport.protocol", "smtp");
        // 主机名（邮件域名地址）
        properties.put("mail.smtp.host", "smtp.qq.com");
        // 端口号
        properties.put("mail.smtp.port", 25);
        // 是否认证
        properties.put("mail.smtp.auth", "true");
        // 是否使用ssl认证
        // properties.put("mail.smtp.ssl.enable", "true");
        // 设置是否显示debug信息 true 会在控制台显示相关信息
        properties.put("mail.debug", "true");
        // 得到Session会话
        Session session = Session.getInstance(properties);
        // 创建通信连接
        Transport transport = null;
        try {
            transport = session.getTransport();
            transport.connect("bigkangsix@qq.com", "basdq312321asda");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        try {
            message.setFrom(new InternetAddress("bigkangsix@qq.com"));
            // 设置收件人邮箱地址
            // 发送多个收件人
            // message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress("123@qq.com"), new InternetAddress("234@qq.com")});
            // 发送单个收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress("446728012@qq.com"));
            // 设置邮件标题
            message.setSubject("测试邮件");
            // 设置邮件内容
            message.setText("测试邮件");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            transport.sendMessage(message,message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
```

​		设置发送图片+文字

```java
            // 一个Multipart对象包含一个或多个bodypart对象，组成邮件正文
            MimeMultipart imageAndTextContent = new MimeMultipart();

            // 创建文本节点
            MimeBodyPart text = new MimeBodyPart();
            text.setContent("发送的测试消息下面是一张图片 <a href='http://baidu.com'>百度一下</a><br/><img src='cid:test_img'/>","text/html;charset=UTF-8");

            // 读取本地图片,将图片数据添加到主主体片段
            MimeBodyPart image = new MimeBodyPart();
            DataHandler dataHandler1 = new DataHandler(new FileDataSource("/Users/bigkang/Documents/笔记/个人资源地址/bigkang.png"));
            image.setDataHandler(dataHandler1);
            image.setContentID("test_img");

            // related(关联的，图片文字)，mixed（混合，带附件）
            imageAndTextContent.setSubType("related");
            message.setContent(imageAndTextContent);
```

​		设置图片+文字+附件

```java
           	// 一个Multipart对象包含一个或多个bodypart对象，组成邮件正文
            MimeMultipart imageAndTextContent = new MimeMultipart();

            // 创建文本节点
            MimeBodyPart text = new MimeBodyPart();
            text.setContent("发送的测试消息下面是一张图片 <a href='http://baidu.com'>百度一下</a><br/><img src='cid:test_img'/>","text/html;charset=UTF-8");

            // 读取本地图片,将图片数据添加到主主体片段
            MimeBodyPart image = new MimeBodyPart();
            DataHandler dataHandler1 = new DataHandler(new FileDataSource("/Users/bigkang/Documents/笔记/个人资源地址/bigkang.png"));
            image.setDataHandler(dataHandler1);
            image.setContentID("test_img");

            // 创建附件
            MimeBodyPart attachPngFile = new MimeBodyPart();
            attachPngFile.setDataHandler(new DataHandler(new FileDataSource(new File("/Users/bigkang/Documents/笔记/个人资源地址/bigkang.png"))));
            // 设置附件名称
            attachPngFile.setFileName(MimeUtility.encodeText("测试附件.png"));

            // 将文本以及图片添加到imageAndTextContent中
            imageAndTextContent.addBodyPart(text);
            imageAndTextContent.addBodyPart(image);
            imageAndTextContent.setSubType("related");

            // 将imageAndTextContent封装成一个片段给multipartMixed引用
            MimeBodyPart textAndImage = new MimeBodyPart();
            textAndImage.setContent(imageAndTextContent);

            // 新建一个混合的消息textAndImage（图片+文字）+ 附件
            MimeMultipart multipartMixed = new MimeMultipart();
            // 添加图片+文字
            multipartMixed.addBodyPart(textAndImage);
            // 添加附件
            multipartMixed.addBodyPart(attachPngFile);
            multipartMixed.setSubType("mixed");

            // 设置消息内容
            message.setContent(multipartMixed);

```

