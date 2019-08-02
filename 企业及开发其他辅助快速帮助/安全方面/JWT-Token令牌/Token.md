# 什么是Token

​		JSON Web Token（JWT）是一个非常轻巧的规范，这个规范允许我们在用户和服务之间传递可靠消息的信息，一个JWT实际上就是一个字符串，他由头部，荷载和签名组成。

​		头部（Header）

​			头部用于描述该JWT的最基本信息，他是用的算法是HS256算法，然后编码使用BASE64编码

​		荷载（Playload）

​			是主体部分，意为载体，承载着有效的 JWT 数据包，它包含三个部分，标准声明，公共声明，私有声明

​			标准声明的字段（标准中建议使用这些字段，但不强制。）

```typescript
  iss?: string; // JWT的签发者
  sub?: string; // JWT所面向的用户
  aud?: string; // 接收JWT的一方
  exp?: number; // JWT的过期时间
  nbf?: number; // 在xxx日期之间，该JWT都是可用的
  iat?: number; // 该JWT签发的时间
  jti?: number; //JWT的唯一身份标识
```

​			公共声明的字段

​		签名（Signature）

​			签名的目的是用来验证头部和载荷是否被非法篡改。 验签过程描述：读取Header部分并Base64解码，得到签名算法。根据以上方法算出签名，如果签名信息不一致，说明是非法的。

