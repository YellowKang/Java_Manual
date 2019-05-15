package test.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuothToken {

    public static void main(String[] args){
        Claims claims = Jwts.parser()
                //传入盐密值校验信息
                .setSigningKey("itse")
                //传入token进行解析
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwic3ViIjoi5bCP6amsIiwiaWF0IjoxNTQ2ODYxNTM0LCJleHAiOjE1NDY4NjE1OTQsImFkbWluIjoi6LaF57qn566h55CG5ZGYIn0.xlaNd38-7zZOfHxlTN00mOm_XU59ygSdso6GZWDOHqk")
                //返回一个主体
                .getBody();
        //获取用户id
        System.out.println(claims.getId());
        //获取用户名
        System.out.println(claims.getSubject());
        //获取创建时间
        System.out.println(claims.getIssuedAt());
        //获取过期时间
        System.out.println(claims.getExpiration());
        //获取claims传输过来的信息。根据键值对获取
        System.out.println(claims.get("admin"));
    }

}
