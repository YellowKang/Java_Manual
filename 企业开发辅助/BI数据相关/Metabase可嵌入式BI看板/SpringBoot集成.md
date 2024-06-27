# 引入依赖

​		需要jwt

```xml
       <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-json</artifactId>
            <version>5.8.11</version>
        </dependency>
```



# 配置文件新增配置

```yaml
metabase:
  defaultDashboard: 5
  siteUrl: https://dashboard.bi.cn
  secretKey: aeca5cfb2fb73d753883wqeqweqweqweqwewqd7775bde5
```



# 新建配置类

```java

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HuangKang
 * @date 2023/7/27 09:37:32
 * @describe MetaBase配置文件
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "metabase")
public class MetaBaseProperties {

    /**
     * 默认的DashboardId
     */
    private Integer defaultDashboard;

    /**
     * 秘钥
     */
    private String secretKey;

    /**
     * 站点Url 示例：https://local:8081
     */
    private String siteUrl;

    /**
     * 站点Url拼接路径 示例：https://local:8081/embed/dashboard/ 默认值 /embed/dashboard/
     */
    private String siteUrlPath = "/embed/dashboard/";

    /**
     * url后缀
     */
    private String siteUrlSuffix = "#bordered=true&titled=true";
}

```

# 新建工具类

```java

import cn.hutool.json.JSONObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

/**
 * @author HuangKang
 * @date 2023/7/27 10:24:55
 * @describe MetaBase工具类
 */
@Component
public class MetaBaseUtil {

    private final MetaBaseProperties metaBaseProperties;

    @Autowired
    public MetaBaseUtil(MetaBaseProperties metaBaseProperties) {
        this.metaBaseProperties = metaBaseProperties;
    }

    /**
     * 获取MetaBase IframeUrl
     *
     * @return IframeUrl 路径
     */
    public String getMetaBaseIframeUrl() {
        return getMetaBaseIframeUrl(metaBaseProperties.getDefaultDashboard());
    }

    /**
     * 获取MetaBase IframeUrl
     *
     * @param dashboard dashboard仪表盘ID
     * @return IframeUrl 路径
     */
    public String getMetaBaseIframeUrl(Integer dashboard) {
        return getMetaBaseIframeUrl(dashboard, metaBaseProperties.getDefaultExpMinute());
    }

    /**
     * 获取MetaBase IframeUrl
     *
     * @param dashboard dashboard仪表盘ID
     * @param expMinute 超时时间 按分钟
     * @return IframeUrl 路径
     */
    public String getMetaBaseIframeUrl(Integer dashboard, Integer expMinute) {
        // 设置超时时间按分钟
        Long round = System.currentTimeMillis() / 1000L + (expMinute * 60L);
        // 获取签名
        String metaBaseEncodedSecretKey = Base64.getEncoder().encodeToString(metaBaseProperties.getSecretKey().getBytes());
        JSONObject resource = new JSONObject();
        resource.put("dashboard", dashboard);
        JSONObject payload = new JSONObject();
        payload.put("resource", resource);
        payload.put("params", new JSONObject());
        payload.put("exp", round);
        // 生成Token
        String token = Jwts.builder()
                .setClaims(payload)
                .signWith(SignatureAlgorithm.HS256, metaBaseEncodedSecretKey)
                .setIssuedAt(new Date())
                .compact();
        // 拼接可访问Url
        return metaBaseProperties.getSiteUrl() + metaBaseProperties.getSiteUrlPath() + token + metaBaseProperties.getSiteUrlSuffix();
    }

}

```

# 新建控制器

```java

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuangKang
 * @date 2023/7/27 10:50:43
 * @describe MetaBase控制器
 */
@RestController
@RequestMapping("manager/metabase")
@Api(tags = "MetaBase控制器")
public class MetaBaseController {

    private final MetaBaseUtil metaBaseUtil;

    @Autowired
    public MetaBaseController(MetaBaseUtil metaBaseUtil) {
        this.metaBaseUtil = metaBaseUtil;
    }

    @GetMapping("getDashboard/{dashboardId}")
    @ApiOperation("查询仪表盘")
    public ResponseData<String> searchCaseQuality(@PathVariable("dashboardId") Integer dashboardId) {
        return ResponseData.success(metaBaseUtil.getMetaBaseIframeUrl(dashboardId));
    }


}

```

