## 引入依赖

​	

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
```

## 配置文件

```
spring.mail.host=smtp.qq.com
spring.mail.username=bigkangsix@qq.com
#次密码为授权码，不是qq密码
spring.mail.password=weiddotviehqiefg

#启用验证
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.starttls.required=fasle
```



## 实现邮件发送

```邮件发送实现
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

这样我们就实现了简单的一个邮件的发送，我们还可以整合其他的技术然后来发送邮件