# 引入依赖

SpringBoot依赖自行引入，注意安装lombok插件

```xml
        <dependency>
            <groupId>com.aliyun</groupId>
            <artifactId>aliyun-java-sdk-core</artifactId>
            <version>4.0.3</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
            <optional>true</optional>
        </dependency>
				<dependency>
   					<groupId>org.apache.commons</groupId>
    				<artifactId>commons-lang3</artifactId>
    				<version>3.1</version>
				</dependency>

```

# 编写配置

properties版本

```properties
# 你的access-key-id
sms.access-key-id=11111
# 你的access-secret
sms.access-secret=22222
# 默认签名
sms.default-sign-name=小康科技
# 默认模板Code
sms.default-temp-code=code1
# 地区（默认值：cn-hangzhou），可无需修改
sms.region=cn-hangzhou
# API地址域名（默认值：dysmsapi.aliyuncs.com），可无需修改
sms.domain=dysmsapi.aliyuncs.com
# 版本（默认值：2017-05-25），可无需修改
sms.version=2017-05-25
```

yaml版本

```properties
sms:
	# 你的access-key-id
  access-key-id: 11111
  # 你的access-secret
  access-secret: 22222
  # 默认签名
  default-sign-name: 小康科技
  # 默认模板Code
  default-temp-code: code1
  # 地区（默认值：cn-hangzhou），可无需修改
  domain: dysmsapi.aliyuncs.com
  # API地址域名（默认值：dysmsapi.aliyuncs.com），可无需修改
  region: cn-hangzhou
  # 版本（默认值：2017-05-25），可无需修改
  version: 2017-05-25
```

# 编写工具类

新建Java类在SpringBoot所扫描的包下，命名为AliYunSmsUtil或者AliYunSmsConfig，此处采用AliYunSmsUtil

```java
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author BigKang
 * @Date 2020/5/12 10:24 上午
 * @Summarize 阿里云短信发送工具类
 *          <dependency>
 *             <groupId>com.aliyun</groupId>
 *             <artifactId>aliyun-java-sdk-core</artifactId>
 *             <version>4.0.3</version>
 *         </dependency>
 */
@Component
@ConfigurationProperties(prefix = "sms")
@Data
public class AliYunSmsUtil {

    /**
     * 地区
     */
    private String region = "cn-hangzhou";

    /**
     * accessKeyId Api调用秘钥，必填
     */
    private String accessKeyId;

    /**
     * accessSecret Api调用秘钥，必填
     */
    private String accessSecret;

    /**
     * Api地址域名
     */
    private String domain = "dysmsapi.aliyuncs.com";

    /**
     * 行动操作（发送短信常量）
     */
    private static final String ACTION_SEND_SMS = "SendSms";

    /**
     * 版本
     */
    private String version = "2017-05-25";

    /**
     * 默认模板Code
     */
    private String defaultTempCode;

    /**
     * 默认签名名称
     */
    private String defaultSignName;

    /**
     * 默认配置初始化
     */
    private DefaultProfile profile = DefaultProfile.getProfile(region,accessKeyId,accessSecret);

    /**
     * 初始化client
     */
    private IAcsClient client = new DefaultAcsClient(profile);

    public AliYunSmsUtil(){
    }

    public AliYunSmsUtil(String accessKeyId,String accessSecret){
        this.accessKeyId = accessKeyId;
        this.accessSecret = accessSecret;
    }


    /**
     * 重载短信发送方法
     * @param templateParam 模板参数
     * @param phoneNumbers 手机号码列表
     * @return
     */
    public String sendSms(String templateParam,List<String> phoneNumbers){
        // 使用默认签名，模板Code发送短信
        return sendSms(defaultTempCode,templateParam,phoneNumbers);
    }

    /**
     * 重载短信发送方法
     * @param tempCode 模板Code
     * @param templateParam 模板参数
     * @param phoneNumbers 手机号码列表
     * @return
     */
    public String sendSms(String tempCode,String templateParam,List<String> phoneNumbers){
        // 使用默认签名
        return sendSms(defaultSignName,tempCode,templateParam,phoneNumbers);
    }

    /**
     * 重载短信发送方法
     * @param signName 签名
     * @param tempCode 模板Code
     * @param templateParam 模板参数
     * @param phoneNumbers 手机号码列表
     * @return
     */
    public String sendSms(String signName,String tempCode,String templateParam,List<String> phoneNumbers){
        // 使用默认地区
       return sendSms(region,signName,tempCode,templateParam,phoneNumbers);
    }


    /**
     * 发送短信方法
     * @param region 地区
     * @param signName 签名
     * @param tempCode 模板Code
     * @param templateParam 模板参数
     * @param phoneNumbers 手机号码列表
     * @return
     */
    public String sendSms(String region,String signName,String tempCode,String templateParam,List<String> phoneNumbers){
        CommonRequest request = new CommonRequest();
        // 设置请求类型
        request.setMethod(MethodType.POST);
        // 设置请求地址域名
        request.setDomain(domain);
        // 设置版本
        request.setVersion(version);
        // 设置请求操作（发送短信）
        request.setAction(ACTION_SEND_SMS);
        // 设置地区
        request.putQueryParameter("RegionId",region);
        // 拼接手机号码数组
        StringBuilder strPhoneNumbers = new StringBuilder();
        if(phoneNumbers != null && phoneNumbers.size() > 0){
            if(phoneNumbers.size() > 1000){
                throw new RuntimeException("每次发送短信不能超过1000条！");
            }
            for (int i = 0; i < phoneNumbers.size(); i++) {
                if(i != 0){
                    strPhoneNumbers.append(",");
                }
                strPhoneNumbers.append(phoneNumbers.get(i));
            }
        }else {
            throw new RuntimeException("短信发送手机号码不能为空！");
        }
        // 设置手机号
        request.putQueryParameter("PhoneNumbers",strPhoneNumbers.toString());

        // 校验签名是否为空
        if(StringUtils.isEmpty(signName)){
            throw new RuntimeException("短信签名不能为空！");
        }else {
            request.putQueryParameter("SignName", signName);
        }

        // 校验模板Code是否为空
        if(StringUtils.isEmpty(tempCode)){
            throw new RuntimeException("短信模板Code不能为空！");
        }else {
            request.putQueryParameter("TemplateCode", tempCode);
        }

        // 设置模板参数
        request.putQueryParameter("TemplateParam",templateParam);

        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getData();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}
```

# 发送短信

我们直接注入AliYunSmsUtil，然后使用默认的签名以及模板Code，类似于验证码的发送

```java

/**
 * @Author BigKang
 * @Date 2020/5/12 3:06 下午
 * @Summarize 测试控制器
 */
@RequestMapping("test")
public class TestController {

    @Autowired
    private AliYunSmsUtil smsUtil;

    @PostMapping("sendSms")
    public String sendSms(List<String> phones){
        return smsUtil.sendSms("{\"code\":11111}",phones);
    }
    
}
```

