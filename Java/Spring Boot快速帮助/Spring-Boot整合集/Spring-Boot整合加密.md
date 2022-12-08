# 配置文件加密

## 引入依赖

```xml
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.3</version>
        </dependency>
```

## 然后编写配置文件

```properties
jasypt:
  encryptor:
    password: bigkang
```

## 然后编写一个控制器

```java
import org.jasypt.encryption.StringEncryptor;
import org.kang.cloud.common.web.commons.response.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author BigKang
 * @Date 2020/6/22 4:22 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 加密计算控制器
 */
@RestController
@RequestMapping("encryptor")
public class EncryptorController {

    private final StringEncryptor stringEncryptor;

    @Autowired
    public EncryptorController(StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    /**
     * 字符串加密
     * @param str 需要加密的字符串
     * @return
     */
    @GetMapping("getStrEncryptor")
    public ResultVo getStrEncryptor(String str) {
        if (StringUtils.isEmpty(str)) {
            throw new NullPointerException("加密字符串不能为空");
        }
        String encrypt = stringEncryptor.encrypt(str);
        return ResultVo.ok(encrypt);
    }

}
```

然后我们请求加密即可

最后将返回的加密信息填入即可

配置文件填入如下

加密前：username:root     password:root

```
spring:
    datasource:
      password: ENC(AXBjsz5G2BQjZlA6CqMvQW/dS39ZllpCBKEfdpRnuJt6gxamOShgavNcqwFlzEvi)
      username: ENC(QC79WrWDn9ja/0vkGrH/gOaQPUKB+iehe+O/AehMNQLB3P9zsLJRvvjQhQWdeFv2)
```

启动项目即可

# 接口加密

## 引入依赖

```xml
<dependency>
    <groupId>cn.licoy</groupId>
    <artifactId>encrypt-body-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
```

## 编写配置文件

```

```

