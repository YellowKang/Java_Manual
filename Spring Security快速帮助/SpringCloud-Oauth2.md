# 什么是Oauth2？

​			OAuth（Open Authorization，开放授权）是为用户资源的授权定义了一个安全、开放及简单的标准，第三方无需知道用户的账号及密码，就可获取到用户的授权信息
 		  OAuth2.0是OAuth协议的延续版本，但不向后兼容OAuth 1.0即完全废止了OAuth1.0。

​			在传统的客户端-服务器身份验证模型中，客户端请求对服务器进行访问限制的资源（受保护的资源） 通过使用资源所有者的服务器向服务器进行身份验证证书。为了提供第三方应用程序访问 受限制的资源，资源所有者与第三方。这会带来一些问题和局限性：

```
	需要第三方应用程序来存储资源
		所有者的凭证供将来使用，通常是密码明文。

​ 尽管要求服务器支持密码验证
		密码固有的安全性弱点。

​ 第三方应用程序获得了对资源的广泛访问
		所有者的受保护资源，从而使资源所有者一无所有 限制持续时间或访问有限子集的能力资源。

​	资源所有者不能撤消对单个第三方的访问权限
		而不撤消所有第三方的访问权限，并且必须这样做更改第三方的密码。

​ 损害任何第三方应用程序会导致以下方面的损害
		最终用户的密码以及受该密码保护的所有数据密码。
```

​			OAuth通过引入授权层解决了这些问题并将客户的角色与资源的角色分开所有者。在OAuth中，客户端请求访问受控资源由资源所有者并由资源服务器托管，并且是发行了与资源不同的一组凭证所有者。

​			就如同我们在百度网盘上也能使用QQ进行登录，但是百度网盘是不知道QQ的用户名密码的，那么他就会向QQ申请Oauth2的第三方协议，允许百度网盘使用QQ进行登录,Oauth2帮助我们解决了第三方的问题，例如我们公司有用户，另一个公司也有用户，但是他们现在想通过我们的系统用户登录到他们的系统，两个不同系统之间用户是不一样的，并且我们不能将用户接口给他们，所以我们可以通过Oauth2的方式给他们添加第三方协议，让他们可以从我们的用户登录跳转到他们的系统，并且通过用户认证。

# 认证流程

官网中的流程图是这样的

![](http://yanxuan.nosdn.127.net/b8059ba5d66f2bf99d3d56623f3eb235.png)

​		首先我们是有一个Client端也就是连接端，我们需要去申请认证，流程大概如下：

```
		1、连接端（Client）发起授权请求给资源拥有者（Resource Owner）
		2、资源拥有者（Resource Owner）认证完毕通过之后，返回给连接端（Client）授权许可
		3、连接端（Client）通过这个授权许可，请求授权服务器（Authorization Server）
		4、授权服务器（Authorization Server）对许可进行校验通过后返回一个访问令牌（Access Token）
		5、连接端（Client）通过访问令牌（Access Token）访问资源服务器（Resource Server）
		6、资源服务器（Resource Server）验证过访问令牌（Access Token）之后返回给连接端（Client）受保护的资源
```

​		那么结合上我自己的理解所画出了一幅图，如下：

![](http://yanxuan.nosdn.127.net/5d1620cca4dc91e6bf4ed642490721e7.png)

# Oauth2的核心概念

## 客户端（Client）

​						需要请求资源的应用客户端，PC，APP，这个也是挺好理解的，也就是我们访问系统采用哪种方式进行登录，有可能是通过Web端，或者APP应用，又或是手机等等。

```
		官网中的解释是这样的：代表以下用户提出受保护资源请求的应用程序：资源所有者及其授权。术语“客户”确实不暗示任何特定的实现特征（例如，应用程序是否在服务器，台式机或其他服务器上执行设备）。
```

## 资源拥有者（Resource Owner）

​						可以是一个人也可以是一个公司实体，对资源持有的实体，我们可以这样理解，首先是自己的系统用户，他可以直接通过用户名和密码的方式进行登录访问，但是其他的第三方需要使用的我们的用户登录，例如QQ，那么资源的拥有者也可以是一个公司，我们通过申请QQ的第三方登录，然后让用户去登录QQ，那么这个时候第三方网站也是一个资源的拥有者，但是这个资源需要我们的QQ用户这个资源拥有者统一授权给第三方网站才能进行使用。

```
		官网中的解释是这样的：能够授予对受保护资源的访问权限的实体，当资源所有者是一个人时，它被称为最终用户。
```

## 授权服务器（Authorization Server）

​						发放令牌的服务，验证资源所有者并获得授权，这个就是我们获取Token的地方，如果是第三方系统，通过QQ登陆成功之后会生成一个Code码，然后回调到第三方系统，那么第三方系统再拿着这个Code去获取我们的Token，然后就可以拿着这个Token去访问QQ的资源了。

```
		官网中的解释是这样的：成功后，服务器向客户机发出访问令牌对资源所有者进行身份验证并获得授权。授权服务器和资源服务器之间的交互超出了本规范的范围。授权服务器可以是与资源服务器相同的服务器，也可以是单独的实体。单个授权服务器可以发出被接受的访问令牌多个资源服务器。
```

## 资源服务器（Resource Server）

​						受保护的资源，可以使用token令牌来访问。这个就是我们常见的需要登录了之后才能使用这个Token去进行访问。

```
		官网中的解释是这样的：托管受保护资源的服务器，能够接受并使用访问令牌响应受保护的资源请求。
```

## 授权许可（Authorization Grant）

​						授权许可在请求资源拥有者之后，资源拥有者同意授权会返回一个授权许可，也就是在QQ中我们使用第三方登录，登陆完毕后，他会提示我们是否授权允许第三方应用登录，如果授权了之后，则会返回给我们一个授权许可。

## 访问令牌（Access Token）

​						在获取到了授权许可（Authorization Grant）之后，我们需要拿着上面的授权许可去申请我们的访问令牌（Access Token），我们在访问系统的时候需要拿着这个访问令牌（Access Token）去请求资源，一旦通过了授权许可校验后就会颁发访问令牌（Access Token），这个时候我们的第三方应用就可以直接拿着访问令牌（Access Token）去请求数据资源了。

## 刷新令牌（Refresh Token）

​						刷新令牌是用于获取访问令牌的凭据。刷新令牌由授权服务器发布给客户端，并且当当前访问令牌时用于获取新的访问令牌变得无效或过期，或获取其他访问令牌范围相同或更窄（访问令牌的范围可能更短）生命周期，且权限少于资源授权所有者）。发行刷新令牌是可选的，具体取决于授权服务器。如果授权服务器发出刷新令牌，它包含在颁发访问令牌时，刷新令牌是一个字符串，代表授予的授权客户由资源所有者负责。字符串通常对客户端。令牌表示用于检索授权信息。与访问令牌不同，刷新令牌是仅用于授权服务器，并且永远不会发送到资源服务器。

这是官网上的Refresh Token流程图

![](http://yanxuan.nosdn.127.net/960ac01cf9610d787b24aa6776498d6b.png)





# Oauth认证方式

​			Oauth2的认证方式分为4种模式，分别是：

​				1、授权码模式（authorization code）

​				2、简化模式（implicit）

​				3、密码模式（resource owner password credentials）

​				4、客户端模式（client credentials）

​			下面分别介绍一下4种模式的认证流程

## 授权码模式

​			授权码模式（authorization code），授权码模式就如同上方的认证流程图一样，那么现在我们来假设一下场景，有一个第三方应用，假设他是一个小说网站（Client），然后他需要使用QQ的第三方登录，而登录的第三方QQ就是我的QQ号码，大概的流程也和图上的6步相同。

​			如下就是该场景的授权码模式：

```
1、小说网站（Client）发起授权请求给QQ的Oauth2，然后跳转到登录页面，我这个资源拥有者（Resource Owner）需要输入QQ号和密码，然后QQ会提示我是否授权给这个小说网站（Client）。

2、我这个资源拥有者（Resource Owner）点击同意以后，会跳转到小说网站指定的页面，并且携带一个Code码授权许可（Authorization Grant）。

3、小说网站（Client）拿到了Code码授权许可（Authorization Grant）之后，请求QQ授权服务器（Authorization Server）。

4、QQ授权服务器（Authorization Server）检查Code码授权许可（Authorization Grant）后，返回访问令牌（Access Token）。
				
5、小说网站（Client）拿到了这个访问令牌（Access Token）之后，会拿着这个访问令牌（Access Token）去请求QQ资源服务器（Resource Server）。

6、QQ资源服务器（Resource Server）检查你这个访问令牌（Access Token）没有问题了之后，就会返回相应的资源（请求的QQ信息）。
```

![](http://yanxuan.nosdn.127.net/5d1620cca4dc91e6bf4ed642490721e7.png)		

​			官方文档解释：

```
							通过使用授权服务器获得授权码作为客户端和资源所有者之间的中介。代替直接从资源所有者，客户端请求授权将资源所有者定向到授权服务器（通过其[ RFC2616 ]中定义的用户代理，该代理继而指导资源所有者将授权码返回给客户端。
							在将资源拥有者定向到客户端之前，授权码，授权服务器对资源所有者并获得授权。因为资源拥有者仅通过授权服务器，资源进行身份验证所有者的凭据永远不会与客户端共享。授权代码提供了一些重要的安全优势，例如对客户端进行身份验证的能力，以及将访问令牌直接传输给客户端，而无需通过资源所有者的用户代理传递它，并可能公开给其他人，包括资源所有者。
```

### 授权码模式认证

首先我们请求认证服务器

```
https://baidu.com/oauth/authorize?
  response_type=code&
  client_id=bigkang&
  redirect_uri=http://bigkang.club&
  scope=web
```

参数解析

```java
		response_type  		// 相应的授权类型
      		code							// 标准的Server授权模式响应Code码模式
      		token							// 脚本的授权响应模式，直接返回token，需要对回调进行校验
    client_id					// 用于表示客户端Id，例如QQ开放给了多个平台第三方登录，但是需要知道是哪一个第三方平台
      		值唯一
    redirect_uri      // 成功或者失败了之后的重定向url路径，到时候会跳转到相应的url
		scpoe							// 表示授权范围，如果相应的client没有这个授权范围则失败
```

然后就可以开始登陆了，例如在百度登录完成之后，点击授权就可以跳转到http://bigkang.club?code=qqiwej1231

code表示返回的code码，也就是我们的授权许可，我们需要拿着这个授权许可去获取访问令牌Token，如下

再次请求

```
https://b.com/oauth/token?
 client_id=CLIENT_ID&
 client_secret=CLIENT_SECRET&
 grant_type=authorization_code&
 code=AUTHORIZATION_CODE&
 redirect_uri=CALLBACK_URL
```

```java
    client_id					// 用于表示客户端Id，例如QQ开放给了多个平台第三方登录，但是需要知道是哪一个第三方平台
      		值唯一
    client_secret			// client客户端盐，用于请求Token，通常不在前端展示，直接通过后台请求
    			盐值
    grant_type				// 授权类型
    			authorization_code			// 授权码类型
      		password								// 密码类型
      		client_credentials			// APP密钥的授权类型
      		refresh_token						// 刷新Token
```

如果我们的Code和信息都是正确的那么服务器将会返回给我们



## 简化模式

## 密码模式

## 客户端模式



# 微服务项目集成Oauth2

## 引入依赖

```xml
         <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
       <!--Oauth2认证依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
        <!--redis依赖存储Token-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```

## 数据库初始化

```
/*
 Navicat Premium Data Transfer

 Source Server         : jd-master
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : 114.67.80.169:3301
 Source Schema         : auth

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 23/06/2020 16:36:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(256) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

```

## 配置文件



## 代码配置

### 认证授权服务器

​			如果我们需要实现认证授权服务器的设置，那么首先我们需要继承AuthorizationServerConfigurerAdapter，这个类，然后重写里面的方法等等，AuthorizationServerConfigurerAdapter如下：

​			我们可以看到AuthorizationServerConfigurerAdapter实现了一个AuthorizationServerConfigurer接口，这个接口分别有3个方法。

```java
/**
 *配置非统组织授权服务器的方便策略。 这种类型的豆类应用于弹簧如果您使用@EnableAuthorizationServer注解，则会进行自动装配上下文}。
 * 
 *@作者戴夫·西尔
 * 
 */
public class AuthorizationServerConfigurerAdapter implements AuthorizationServerConfigurer {

  /**
   *配置授权服务器的安全性，这实际上意味着/oauth/token端点。 的
   */oauth/授权端点也需要安全，但这是一个正常的面向用户的端点，应该是
   *的安全方式与您的其他UI相同，所以这里不包括。 默认设置覆盖最常见的
   *需求，遵循OAuth2规范的建议，所以您不需要在这里做任何事情来获得a
   *基本服务器启动并运行。,可以用来配置认证端点的/oauth/token认证请求方式
   *@Param安全性为安全特性提供一个流畅的配置程序
   */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
	}
 /**
   *配置{@linkClientDetails Service}，例如。 声明单个客户端及其属性。 注意这一点
   除非{@链接身份验证管理器}是，否则不启用	*密码授予(即使允许某些客户端
   *提供给{@链接#configure(AuthorizationServerEndpointsConfigurer)}。 至少一个客户，或一个完整的必须声明形成	*自定义{@linkClientDetailsService}，否则服务器将无法启动。
   *Client连接端的信息定义，可以定义内存或者JDBC等，保存我们Client端的数据
   *@Param客户端，客户端详细信息配置器
   */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
	}

  /**
    *配置授权服务器端点的非安全特性，如令牌存储、令牌
    *自定义、用户批准和授权类型。默认情况下，您不需要做任何事情，除非您需要
    * 认证授权服务器端点配置，用于定义认证授权服务器的Token存储创建，用户查询等等
    *密码授权，在这种情况下，您需要提供一个{@link AuthenticationManager}。
    *
    * @param端点配置器
    */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	}

}
```

#### 配置Client信息数据库存储

设置clients端的信息交由Jdbc数据库进行存储，并且将client的秘钥进行加密

请先初始化数据源以及加密

```java

    /**
     * 注入密码加密
     */
		@Autowired
    private PasswordEncoder passwordEncoder;

		/**
     * 注入数据源
     */
		@Autowired
    private DataSource dataSource;

		/**
     * Client连接端的信息定义，可以定义内存或者JDBC等，保存我们Client端的数据
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 设置Client连接端的信息，如Client_Id，以及盐和跳转的Url等等
        JdbcClientDetailsServiceBuilder jdbc = clients.jdbc(dataSource);
        // 设置盐使用加密
        jdbc.passwordEncoder(passwordEncoder);
    }
```

#### 配置Client信息内存存储



配置









### 资源服务器