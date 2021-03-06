import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Date;

public class Test01 {

    @Test
    public void test01() {
        //加密
        String hashPw = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println("加密后：" + hashPw);
        //验证
        boolean checkPw = BCrypt.checkpw("123456", hashPw);
        System.out.println("密码校验是否成功：" + checkPw);
    }

    @Test
    public void test02() {
        JwtBuilder builder = Jwts.builder()
                .setId("888")   //设置唯一编号
                .setSubject("小白")//设置主题，可以是JSON数据
                .setIssuedAt(new Date())//设置签发日期
                .signWith(SignatureAlgorithm.HS256, "regino");//设置签名，使用HS256算法，并设置SecretKey(字符串)
        //构建，并返回一个字符串
        String jwt = builder.compact();
        System.out.println(jwt);
        //解析Token
        Claims claims = Jwts.parser().setSigningKey("regino").parseClaimsJws(jwt).getBody();
        System.out.println(claims);
    }

    @Test
    public void test03() {
        //当前时间
        long currentTimeMillis = System.currentTimeMillis();
        currentTimeMillis += 1000000L;
        Date date = new Date(currentTimeMillis);
        JwtBuilder builder = Jwts.builder()
                .setId("888")   //设置唯一编号
                .setSubject("小白")//设置主题，可以是JSON数据
                .setIssuedAt(new Date())//设置签发日期
                .setExpiration(date)//设置过期时间
                .claim("roles", "admin")//设置角色
                //可以设置多个
                //.claim("username","reggie")
                .signWith(SignatureAlgorithm.HS256, "regino");//设置签名，使用HS256算法，并设置SecretKey(字符串)
        //构建 并返回一个字符串
        System.out.println(builder.compact());
    }
}
