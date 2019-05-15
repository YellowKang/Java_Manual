package test.JWT;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class CreateToken {

    public static void main(String[] args){
        JwtBuilder jwt = Jwts.builder()
                //设置id
                .setId("1")
                //设置用户
                .setSubject("小马")
                //设置创建时间
                .setIssuedAt(new Date())
                //使用盐秘值
                .signWith(SignatureAlgorithm.HS256,"itse")
                //设置过期时间
                .setExpiration(new Date(new Date().getTime()+120000))
                //自定义用户的权限传输过去
                .claim("admin", "超级管理员");

        System.out.println(jwt.compact());

    }

}

